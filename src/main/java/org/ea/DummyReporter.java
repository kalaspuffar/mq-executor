package org.ea;

public class DummyReporter extends Reporter {

    @Override
    public void reportActivity() throws Exception {
        System.out.println("Dummy");
    }

    public int getPriority() {
        return 0;
    }
}
