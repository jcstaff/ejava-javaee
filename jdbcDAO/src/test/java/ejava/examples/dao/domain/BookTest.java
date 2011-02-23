package ejava.examples.dao.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class BookTest {
    private static String LONG_DESCRIPTION;
    static {
        StringBuilder text = new StringBuilder();
        for (int i=0; i<200; i++) {
            int ch = 'A' + (i % 26);
            text.append(ch);
        }
        LONG_DESCRIPTION = text.toString();
    }

    /*
     * Tests the setter/getter for Author
     */
    @Test
    public void testAuthor() {
        String testName = "1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 ";
        Book book = new Book(0);
        book.setAuthor(testName);
        assertEquals(testName, book.getAuthor());
    }

    /*
     * Tests the setter/getter for Description
     */
    @Test
    public void testDescription() {
        Book book = new Book(0);
        book.setDescription(LONG_DESCRIPTION);
        assertEquals(LONG_DESCRIPTION, book.getDescription());
    }

    /*
     * Test method for Id
     */
    @Test
    public void testId() {
        int testId = 3;
        Book book = new Book(testId);
        assertEquals(testId, book.getId());
    }
    
    /*
     * Test method for version
     */
    @Test
    public void testVersion() {
        Book book = new Book(0);
        long version = book.getVersion();
        assertEquals(0, version);
        book.setVersion(++version);
        assertEquals(version, book.getVersion());
    }

    /*
     * Test method for pages
     */
    @Test
    public void testPages() {
        int testPages = 700;
        Book book=new Book(0);
        book.setPages(testPages);
        assertEquals(testPages, book.getPages());
    }

    /*
     * Test method for title
     */
    @Test
    public void testTitle() {
        String testTitle = "a  b c d e f g h i j k l m n o p q r s t u v w x y z";
        Book book = new Book(0);
        book.setTitle(testTitle);
        assertEquals(testTitle, book.getTitle());
    }

    /*
     * Test method for toString()
     */
    @Test
    public void testString() {
        Book book = new Book(0);
        assertNotNull(book.toString());
        assertTrue(book.toString().length() > 0);
    }
}
