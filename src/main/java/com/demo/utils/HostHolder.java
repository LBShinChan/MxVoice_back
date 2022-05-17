package com.demo.utils;

import com.demo.entity.Visitor;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    private ThreadLocal<Visitor> visitors = new ThreadLocal<>();

    public void setVisitor(Visitor visitor) {
        visitors.set(visitor);
    }

    public Visitor getVisitor() {
        return visitors.get();
    }

    public void clear() {
        visitors.remove();
    }
}
