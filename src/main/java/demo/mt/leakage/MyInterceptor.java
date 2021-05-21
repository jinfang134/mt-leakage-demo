package demo.mt.leakage;

import org.jeecgframework.minidao.aspect.EmptyInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class MyInterceptor implements EmptyInterceptor {
    @Override
    public boolean onInsert(Field[] fields, Object obj) {
        return false;
    }

    @Override
    public boolean onUpdate(Field[] fields, Object obj) {
        return false;
    }
}
