package com.azaitsev.widgets;

import com.azaitsev.widgets.repository.widget.WidgetInMemoryRepository;
import com.azaitsev.widgets.repository.widget.WidgetRepository;
import com.azaitsev.widgets.service.widget.WidgetService;
import com.azaitsev.widgets.service.widget.WidgetServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public WidgetRepository getWidgetRepository() {
        return new WidgetInMemoryRepository();
    }

    @Bean
    public WidgetService getWidgetService(WidgetRepository widgetRepository) {
        return new WidgetServiceImpl(widgetRepository);
    }
}
