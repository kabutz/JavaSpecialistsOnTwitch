package eu.javaspecialists.twitch.broadcast1;

class SimpleArrayListTest extends SimpleListTest {
    @Override
    protected SimpleList<String> create() {
        return new SimpleArrayList<>();
    }
}