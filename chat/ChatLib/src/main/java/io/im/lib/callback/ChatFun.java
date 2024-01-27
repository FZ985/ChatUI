package io.im.lib.callback;

public interface ChatFun {

    public interface Fun {
        void apply();
    }

    public interface Fun1<T> {
        void apply(T t);
    }

    public interface Fun2<A, B> {
        void apply(A a, B b);
    }

    public interface Fun3<A, B, C> {
        void apply(A a, B b, C c);
    }

    public interface Fun4<A, B, C, D> {
        void apply(A a, B b, C c, D d);
    }

}
