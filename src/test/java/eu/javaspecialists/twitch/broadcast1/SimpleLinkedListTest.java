package eu.javaspecialists.twitch.broadcast1;

class SimpleLinkedListTest extends SimpleListTest {
    @Override
    protected SimpleList<String> create() {
        return new SimpleLinkedList<>();
    }
}