package xin.wenjing.waline;

import org.springframework.stereotype.Component;
import run.halo.app.plugin.BasePlugin;
import run.halo.app.plugin.PluginContext;

/**
* 插件启动入口
* @author: dreamChaser
* @date: 2024/7/7 20:30
*/
@Component
public class WalinePlugin extends BasePlugin {

    public WalinePlugin(PluginContext pluginContext) {
        super(pluginContext);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
}
