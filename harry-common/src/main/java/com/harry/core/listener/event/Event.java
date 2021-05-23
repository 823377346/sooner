package com.harry.core.listener.event;

import org.springframework.context.ApplicationEvent;

/**
 *
 * @Author zhuxiaoyun
 * @Date 2020/3/12 11:07
 */
public class Event<S,T> extends ApplicationEvent {
    private final S source;
    private final T content;

    public Event(final S source, final T content) {
        super(source);
        this.source = source;
        this.content = content;
    }
    @Override
    public S getSource(){
        return this.source;
    }
    public T getContent() {
        return content;
    }
}
