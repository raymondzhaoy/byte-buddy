package com.blogspot.mydailyjava.bytebuddy.instrumentation;

import com.blogspot.mydailyjava.bytebuddy.dynamic.DynamicType;
import com.blogspot.mydailyjava.bytebuddy.instrumentation.method.bytecode.bind.annotation.Super;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MethodDelegationSuperTest extends AbstractInstrumentationTest {

    private static final String FOO = "foo", BAR = "bar", QUX = "qux";

    public static interface Qux {

        Object qux();
    }

    public static class Foo implements Qux {

        @Override
        public Object qux() {
            return FOO;
        }
    }

    public static class Baz {

        public static String baz(@Super Foo foo) {
            return foo.qux() + QUX;
        }
    }

    @Test
    public void testSuperInstance() throws Exception {
        DynamicType.Loaded<Foo> loaded = instrument(Foo.class, MethodDelegation.to(Baz.class));
        Foo instance = loaded.getLoaded().newInstance();
        assertThat(instance.qux(), is((Object) (FOO + QUX)));
    }

    public static class FooBar {

        public static String baz(@Super Qux foo) {
            return foo.qux() + QUX;
        }
    }

    @Test
    public void testSuperInterface() throws Exception {
        DynamicType.Loaded<Foo> loaded = instrument(Foo.class, MethodDelegation.to(FooBar.class));
        Foo instance = loaded.getLoaded().newInstance();
        assertThat(instance.qux(), is((Object) (FOO + QUX)));
    }

    public static class QuxBaz {

        public static String baz(@Super(strategy = Super.Instantiation.UNSAFE) Foo foo) {
            return foo.qux() + QUX;
        }
    }

    @Test
    public void testSuperInstanceUnsafe() throws Exception {
        DynamicType.Loaded<Foo> loaded = instrument(Foo.class, MethodDelegation.to(QuxBaz.class));
        Foo instance = loaded.getLoaded().newInstance();
        assertThat(instance.qux(), is((Object) (FOO + QUX)));
    }

    public static class Bar extends Foo {

        @Override
        public String qux() {
            return BAR;
        }
    }

    @Test
    public void testBridgeMethodResolution() throws Exception {
        DynamicType.Loaded<Bar> loaded = instrument(Bar.class, MethodDelegation.to(Baz.class));
        Bar instance = loaded.getLoaded().newInstance();
        assertThat(instance.qux(), is(BAR + QUX));
    }
}
