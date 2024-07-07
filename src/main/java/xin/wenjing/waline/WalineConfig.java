package xin.wenjing.waline;

import lombok.Data;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;

/**
 * 功能描述
 * 插件配置信息
 * @author: dreamChaser
 * @date: 2024年07月07日 10:35
 */
public class WalineConfig {

    public static Mono<ConfigDetail> fetchWalineBaseConf(ReactiveSettingFetcher settingFetcher){
        return  settingFetcher.fetch(ConfigDetail.GROUP, ConfigDetail.class).defaultIfEmpty(new ConfigDetail());
    }

    @Data
    static class ConfigDetail {
        public static final String GROUP = "baseConf";
        private String walineUrl;
        private boolean staticResourceType;
        private String jsUrl;
        private String cssUrl;
        private String lightDarkMode;
        private String locale;
        private boolean enableReaction;
        private String reactionMode;
        private String customReaction ;
        private boolean enablePageView;
    }

}
