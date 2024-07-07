package xin.wenjing.waline;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.dialect.CommentWidget;
import java.util.Properties;

/**
 * 接入 waline 评论系统
 * @author: dreamChaser
 * @date: 2024年07月07日 10:07
 */
@RequiredArgsConstructor
@Component
public class WalineComment implements CommentWidget {

    private final ReactiveSettingFetcher settingFetcher;
    static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}");

    @Override
    public void render(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {

        WalineConfig.ConfigDetail configDetail = WalineConfig.fetchWalineBaseConf(settingFetcher).blockOptional().orElseThrow();
        structureHandler.replaceWith(walineClientScript(configDetail), false);
    }

    public String walineClientScript(WalineConfig.ConfigDetail baseConf){

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
        final String walineInitScript =
                    """
                            <div id="waline-comment">
                            </div>
                            <link href='${cssUrl}' rel='stylesheet' />
                            <script type="module" data-pjax defer>
                                import {init} from "${jsUrl}";
                                function initWaline(){
                                     init({
                                        el: '#waline-comment',
                                        serverURL: "${walineUrl}",
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
}
