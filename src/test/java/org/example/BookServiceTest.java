package org.example;

import org.junit.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class BookServiceTest {
    private static BookService bookService;
    private User testUser;
    private Book testBook;

//MOCKED OBJECT OF THE USERDATABASE CLASS
    @Mock
    private UserDatabase userDatabase;

//TEST SETUP AND TEARDOWN
//(to ensure that each test method starts with
// a fresh set of objects and a clean book database)

    @BeforeClass
    public static void setUpClass() {
        MockitoAnnotations.openMocks(BookServiceTest.class);
        bookService = new BookService();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testUser = new User("testUser", "testPassword", "test@google.com");
        testBook = new Book("Test Book", "Test Author", "Test Genre", 10.99);
    }

    @After
    public void tearDown() {
        testUser = null;
        testBook = null;
        bookService.getBookDatabase().clear();
    }

//POSITIVE CASE WHERE BOOKS IS ADDED THEN SEARCH USING A KEYWORD "AUTHOR"
    @Test
    public void testSearchBook_Positive() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>(List.of(book1, book2))); //MOCK BEHAVIOR OF RETRIEVING THE BOOK DATABASE

        List<Book> searchResult = bookService.searchBook("Author");
        Assert.assertEquals(2, searchResult.size()); //VERIFYING-TWO BOOKS AND COMPARE WITH SEARCH LIST
        Assert.assertTrue(searchResult.contains(book1)); // VERIFY SEARCH RESULT LIST CONTAINS BOOK1
        Assert.assertTrue(searchResult.contains(book2)); // VERIFY SEARCH RESULT LIST CONTAINS BOOK2

        verify(bookService).getBookDatabase();
    }

//NEGATIVE CASE WHERE THE BOOK DATABASE IS EMPTY, SEARCHED KEYWORD
    @Test
    public void testSearchBook_Negative() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>()); //MOCK BEHAVIOR-WHEN METHOD CALL-RETURN EMPTY ARRAY

        List<Book> searchResult = bookService.searchBook("Author");
        Assert.assertEquals(0, searchResult.size()); //VERIFY SEARCH IS EMPTY. COMPARES EXPECTED 0 WITH SEARCH.

        verify(bookService).getBookDatabase();
    }

//EDGE CASE-ADDS A BOOK TO THE DATA BASE THEN SEARCH FOR BOOK BY AN EMPTY KEYBOARD
    @Test
    public void testSearchBook_EdgeCase() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>(List.of(book))); //MOCK BEHAVIOR-WHEN METHOD CALL-RETURN NEW ARRAY LIST

        List<Book> searchResult = bookService.searchBook("");
        Assert.assertEquals(1, searchResult.size());
        Assert.assertTrue(searchResult.contains(book));

        verify(bookService).getBookDatabase();
    }

//POSITIVE CASE-ADDS BOOK TO DATABASE AND ATTEMPTS TO PURCHASE IT-SUCCESS
    @Test
    public void testPurchaseBook_Success() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>(List.of(testBook)));

        boolean result = bookService.purchaseBook(testUser, testBook);
        Assert.assertTrue(result);

        verify(bookService).getBookDatabase();
    }

//NEGATIVE CASE-BOOK PURCHASED DOES NOT EXIST IN THE DATABASE
    @Test
    public void testPurchaseBook_Failure() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>());

        boolean result = bookService.purchaseBook(testUser, testBook);
        Assert.assertFalse(result);

        verify(bookService).getBookDatabase();
    }

//POSITIVE CASE- ADDS BOOK, PURCHASE BOOK, ADDS A REVIEW, SUCCESS
    @Test
    public void testAddBookReview_Success() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>(List.of(testBook)));
        when(userDatabase.getPurchasedBooks(testUser)).thenReturn(new ArrayList<>(List.of(testBook)));

        boolean result = bookService.addBookReview(testUser, testBook, "Great book, highly recommend!");
        Assert.assertTrue(result);
        Assert.assertEquals(1, testBook.getReviews().size());
        Assert.assertEquals("Great book, highly recommend!", testBook.getReviews().get(0));

        verify(bookService).getBookDatabase();
        verify(userDatabase).getPurchasedBooks(testUser);
    }

//NEGATIVE CASE- ADDS A REVIEW BUT BOOK HAS NOT BEEN PURCHASED
    @Test
    public void testAddBookReview_Failure() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>(List.of(testBook)));
        when(userDatabase.getPurchasedBooks(testUser)).thenReturn(new ArrayList<>());

        boolean result = bookService.addBookReview(testUser, testBook, "Great book!");
        Assert.assertFalse(result);
        Assert.assertEquals(0, testBook.getReviews().size());

        verify(bookService).getBookDatabase();
        verify(userDatabase).getPurchasedBooks(testUser);
    }

//POSITIVE CASE-ADDS BOOK TO DATABASE-SUCCESS
    @Test
    public void testAddBook_Success() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>());

        boolean result = bookService.addBook(testBook);
        Assert.assertTrue(result);
        Assert.assertTrue(bookService.searchBook("Test Book").contains(testBook));

        verify(bookService).getBookDatabase();
    }

//NEGATIVE CASE-ADDING A SAME BOOK TO DATABASE-FAILURE
    @Test
    public void testAddBook_Failure() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>(List.of(testBook)));

        boolean result = bookService.addBook(testBook);
        Assert.assertFalse(result);

        verify(bookService).getBookDatabase();
    }

//POSITIVE CASE-ADDS BOOK TO DATABASE, REMOVES BOOK, SUCCESS-NON SEARCHABLE
    @Test
    public void testRemoveBook_Success() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>(List.of(testBook)));

        boolean result = bookService.removeBook(testBook);
        Assert.assertTrue(result);
        Assert.assertFalse(bookService.searchBook("Test Book").contains(testBook));

        verify(bookService).getBookDatabase();
    }

//NEGATIVE CASE-REMOVING A BOOK THAT DOES NOT EXIST IN DATABASE-FALSE
    @Test
    public void testRemoveBook_Failure() {
        when(bookService.getBookDatabase()).thenReturn(new ArrayList<>());

        boolean result = bookService.removeBook(testBook);
        Assert.assertFalse(result);

        verify(bookService).getBookDatabase();
    }
}



//
//CODE WITHOUT MOCKING OBJECTS (TEST-SUCCESSFUL)
//package org.example;
//
//import org.junit.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class BookServiceTest {
//    private static BookService bookService;
//    private User testUser;
//    private Book testBook;
//
//    //TEST SETUP AND TEARDOWN
////(to ensure that each test method starts with
//// a fresh set of objects and a clean book database)
//    @BeforeClass
//    public static void setUpClass() {
//        // Initialize the BookService instance (runs once before any test methods)
//        bookService = new BookService();
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//        // Clean up any resources after all tests have run (runs once after all test methods)
//        bookService = null;
//    }
//
//    //TEST METHOD----
//    @Before
//    public void setUp() {
//        testUser = new User("testUser", "testPassword", "test@google.com");
//        testBook = new Book("Test Book", "Test Author", "Test Genre", 10.99);
//    }
//
//    @After
//    public void tearDown() {
//        testUser = null;
//        testBook = null;
//        bookService.getBookDatabase().clear();
//    }
//
//    //POSITIVE CASE WHERE BOOKS IS ADDED THEN SEARCH USING A KEYWORD "AUTHOR"
//    @Test
//    public void testSearchBook_Positive() {
//        // ADDING BOOKS TO THE DATABASE
//        Book book1 = new Book("Book 1", "Author 1", "Genre 1", 12.99);
//        Book book2 = new Book("Book 2", "Author 2", "Genre 2", 14.99);
//        bookService.addBook(book1);
//        bookService.addBook(book2);
//
//
//        List<Book> searchResult = bookService.searchBook("Author");
//        Assert.assertEquals(2, searchResult.size());
//        Assert.assertTrue(searchResult.contains(book1));
//        Assert.assertTrue(searchResult.contains(book2));
//    }
//
//    //NEGATIVE CASE WHERE THE BOOK DATABASE IS EMPTY, SEARCHED KEYWORD
//    @Test
//    public void testSearchBook_Negative() {
//        List<Book> searchResult = bookService.searchBook("Author");
//        Assert.assertEquals(0, searchResult.size());
//    }
//
//    //EDGE CASE-ADDS A BOOK TO THE DATA BASE THEN SEARCH FOR BOOK BY AN EMPTY KEYBOARD
//    @Test
//    public void testSearchBook_EdgeCase() {
//
//        Book book = new Book("Book 1", "Author 1", "Genre 1", 12.99);
//        bookService.addBook(book);
//
//        List<Book> searchResult = bookService.searchBook("");
//        Assert.assertEquals(1, searchResult.size());
//        Assert.assertTrue(searchResult.contains(book));
//    }
//
//    //POSITIVE CASE-ADDS BOOK TO DATABASE AND ATTEMPTS TO PURCHASE IT-SUCCESS
//    @Test
//    public void testPurchaseBook_Success() {
//        // Add the book to the book database
//        bookService.addBook(testBook);
//
//        // Purchase the book
//        boolean result = bookService.purchaseBook(testUser, testBook);
//        Assert.assertTrue(result);
//    }
//    //NEGATIVE CASE-BOOK PURCHASED DOES NOT EXIST IN THE DATABASE
//    @Test
//    public void testPurchaseBook_Failure() {
//        boolean result = bookService.purchaseBook(testUser, testBook);
//        Assert.assertFalse(result);
//    }
//    //POSITIVE CASE- ADDS BOOK, PURCHASE BOOK, ADDS A REVIEW, SUCCESS
//    @Test
//    public void testAddBookReview_Success() {
//
//        bookService.addBook(testBook);
//
//        bookService.purchaseBook(testUser, testBook);
//
//        boolean result = bookService.addBookReview(testUser, testBook, "Great book!");
//        Assert.assertTrue(result);
//        Assert.assertEquals(1, testBook.getReviews().size());
//        Assert.assertEquals("Great content, highly recommended!", testBook.getReviews().get(0));
//    }
//
//    //NEGATIVE CASE- ADDS A REVIEW BUT BOOK HAS NOT BEEN PURCHASED
//    @Test
//    public void testAddBookReview_Failure() {
//
//        bookService.addBook(testBook);
//
//        boolean result = bookService.addBookReview(testUser, testBook, "Great book!");
//        Assert.assertFalse(result);
//        Assert.assertEquals(0, testBook.getReviews().size());
//    }
//
//    //POSITIVE CASE-ADDS BOOK TO DATABASE-SUCCESS
//    @Test
//    public void testAddBook_Success() {
//        boolean result = bookService.addBook(testBook);
//        Assert.assertTrue(result);
//        Assert.assertTrue(bookService.searchBook("Test Book").contains(testBook));
//    }
//
//    //NEGATIVE CASE-ADDING A SAME BOOK TO DATABASE-FAILURE
//    @Test
//    public void testAddBook_Failure() {
//        bookService.addBook(testBook);
//
//        boolean result = bookService.addBook(testBook);
//        Assert.assertFalse(result);
//    }
//
//    //POSITIVE CASE-ADDS BOOK TO DATABASE, REMOVES BOOK, SUCCESS-NON SEARCHABLE
//    @Test
//    public void testRemoveBook_Success() {
//        bookService.addBook(testBook);
//
//        boolean result = bookService.removeBook(testBook);
//        Assert.assertTrue(result);
//        Assert.assertFalse(bookService.searchBook("Test Book").contains(testBook));
//    }
//
//    //NEGATIVE CASE-REMOVING A BOOK THAT DOES NOT EXIST IN DATABASE-FALSE
//    @Test
//    public void testRemoveBook_Failure() {
//        boolean result = bookService.removeBook(testBook);
//        Assert.assertFalse(result);
//    }
//}