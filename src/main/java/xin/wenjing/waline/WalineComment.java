package xin.wenjing.waline;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.PropertyPlaceholderHelper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.dialect.CommentWidget;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 接入 waline 评论系统
 * @author: dreamChaser
 * @date: 2024年07月07日 10:07
 */
@RequiredArgsConstructor
@Component
public class WalineComment implements CommentWidget {

    private static final String TEMPLATE_ID_VARIABLE = "_templateId";

    private final ReactiveSettingFetcher settingFetcher;

    static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}");

    @Override
    public void render(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {

        WalineConfig.ConfigDetail configDetail = WalineConfig.fetchWalineBaseConf(settingFetcher).blockOptional().orElseThrow();
        WalineConfig.AdvanceSettings advanceConfig = WalineConfig.fetchWalineAdvanceConf(settingFetcher).blockOptional().orElseThrow();
        String multiCommentDom = multiCommentDomId(tag, context, advanceConfig.isAdaptMultiComment(), advanceConfig.getMultiCommentPage());
        structureHandler.replaceWith(walineClientScript(configDetail, multiCommentDom, context), false);
    }

    public String walineClientScript(WalineConfig.ConfigDetail baseConf, String multiCommentDom, ITemplateContext context){

        final Properties properties = new Properties();
        properties.setProperty("walineUrl", baseConf.getWalineUrl());
        properties.setProperty("locale", baseConf.getLocale());
        if(baseConf.isEnableReaction()){
            String renderReaction = baseConf.getReactionMode().equals("default") ? "true" : baseConf.getCustomReaction();
            properties.setProperty("reaction", renderReaction);
        }

        // waline静态资源地址引入判断
        if(baseConf.isStaticResourceType()){
            properties.setProperty("jsUrl", baseConf.getJsUrl());
            properties.setProperty("cssUrl", baseConf.getCssUrl());
        }else{
            properties.setProperty("jsUrl", "/plugins/plugin-waline/assets/static/v3/waline.js");
            properties.setProperty("cssUrl", "/plugins/plugin-waline/assets/static/v3/waline.css");
        }

        properties.setProperty("lightDarkMode", baseConf.getLightDarkMode());
        properties.setProperty("enablePageView", String.valueOf(baseConf.isEnablePageView()));

        if(multiCommentDom != null){
            properties.setProperty("mountedNode", multiCommentDom);
            String curTemplateId = getTemplateId(context);
            properties.setProperty("pageKey", "/" + curTemplateId + "/" + multiCommentDom);
            properties.setProperty("pageKeyType", "normalStr");
        }else{
            properties.setProperty("mountedNode", "waline-comment");
            properties.setProperty("pageKeyType", "functionStr");
        }

        final String walineInitScript =
                    """
                            <div id="${mountedNode}">
                            </div>
                            <link href='${cssUrl}' rel='stylesheet' />
                            <script type="module" data-pjax defer>
                                import {init} from "${jsUrl}";
                                let pageKeyResolve = "";
                                if("${pageKeyType}" == "functionStr"){
                                    pageKeyResolve = window.location.pathname.replace(/\\/page\\/\\d$/, "");
                                }else if("${pageKeyType}" == "normalStr"){
                                    pageKeyResolve = "${pageKey}";
                                }
                                function initWaline(){
                                     init({
                                        el: '#${mountedNode}',
                                        serverURL: "${walineUrl}",
                                        path: pageKeyResolve,
                                        lang: "zh-CN",
                                        dark: '${lightDarkMode}',
                                        copyright: false,
                                        pageview: ${enablePageView},
                                        reaction: ${reaction},
                                        locale: ${locale}
                                    })
                                }
                                document.addEventListener("DOMContentLoaded",()=>{
                                    initWaline();
                                })
                                document.addEventListener("pjax:complete",()=>{
                                    initWaline();
                                })
                            </script>
                        """;
        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(walineInitScript, properties);
    }

    /**
     * 评论唯一标识生成
     * @param tag
     * @return
     */
    private String multiCommentDomId(IProcessableElementTag tag, ITemplateContext context, boolean isAdaptMultiComment, List<WalineConfig.AdvanceSettings.MultiCommentPages> multiCommentPage){
        if(!isAdaptMultiComment){
            return null;
        }
        String templateId = getTemplateId(context);
        List<WalineConfig.AdvanceSettings.MultiCommentPages> patchTemplate = multiCommentPage
            .stream()
            .filter(template -> template.getTemplateName().equals(templateId))
            .collect(Collectors.toList());
        if(patchTemplate.size() == 0){
            return null;
        }
        IAttribute groupAttribute = tag.getAttribute("group");
        IAttribute kindAttribute = tag.getAttribute("kind");
        IAttribute nameAttribute = tag.getAttribute("name");

        String group = groupAttribute.getValue() == null ? "" : StringUtils.defaultString(groupAttribute.getValue());
        String name = kindAttribute.getValue();
        String kind = nameAttribute.getValue();
        Assert.notNull(name, "The name must not be null.");
        Assert.notNull(kind, "The kind must not be null.");
        String groupKindNameAsDomId = String.join("-", group, kind, name);
        String commentId = "artalk-" + groupKindNameAsDomId.replaceAll("[^\\-_a-zA-Z0-9\\s]", "-")
            .replaceAll("(-)+", "-");
        return commentId;
    }

    /**
     * 返回模版名称
     * @param context
     * @return
     */
    public static String getTemplateId(ITemplateContext context) {
        try {
            String  templateName = context.getVariable(TEMPLATE_ID_VARIABLE).toString();
            return templateName != null && templateName.length() > 0 ? templateName : "";
        }catch (Exception e){
            return "";
        }
    }

}
