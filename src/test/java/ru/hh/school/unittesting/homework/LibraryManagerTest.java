package ru.hh.school.unittesting.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


class LibraryManagerTest {

  private NotificationService notificationService;
  private UserService userService;

  private LibraryManager libManager;

  @BeforeEach
  void setUp() {
    //мокаем поля сервисов, дефолтная реализация методов интерфейса будет возвращать дефолтные значения 0, false, null
    //можно через аннотации @Mock, @InjectMocks, @ExtendWith(MockitoExtension.class) , но мне захотелось явно вызвать
    notificationService = mock(NotificationService.class);
    userService = mock(UserService.class);
    libManager = new LibraryManager(notificationService, userService);
  }

  @Test
  void addingBooks() {
    libManager.addBook("The Grapes of Wrath", 1);

    when(userService.isUserActive(any())).thenReturn(true);
    assertTrue(libManager.borrowBook("The Grapes of Wrath", "user1"));
    assertFalse(libManager.borrowBook("The Grapes of Wrath", "user2"));

    verify(userService, times(1)).isUserActive("user1");
    verify(userService, times(1)).isUserActive("user2");
    verify(notificationService, times(1)).notifyUser("user1", "You have borrowed the book: The Grapes of Wrath");
  }

  @Test
  void getBooks() {
    libManager.addBook("The Devils", 10);
    assertEquals(10, libManager.getAvailableCopies("The Devils"));
    verifyNoInteractions(userService, notificationService);
  }

  @Test
  void borrowBookByNonActiveUser() {
    libManager.addBook("Either/Or Kierkegaard", 1);
    assertFalse(libManager.borrowBook("Either/Or Kierkegaard", "user1"));
    verify(notificationService, times(1)).notifyUser("user1", "Your account is not active.");
  }

  @Test
  void returnBooks() {
    libManager.addBook("Les Mémoires du Diable", 1);

    when(userService.isUserActive(any())).thenReturn(true);
    libManager.borrowBook("Les Mémoires du Diable", "user1");
    verify(notificationService, times(1)).notifyUser("user1", "You have borrowed the book: Les Mémoires du Diable");
    assertEquals(0, libManager.getAvailableCopies("Les Mémoires du Diable"));

    assertTrue(libManager.returnBook("Les Mémoires du Diable", "user1"));
    verify(notificationService, times(1)).notifyUser("user1", "You have returned the book: Les Mémoires du Diable");
    assertEquals(1, libManager.getAvailableCopies("Les Mémoires du Diable"));
  }

  @Test
  void returnNonExistBook() {
    libManager.addBook("Madame Bovary", 1);
    assertFalse(libManager.returnBook("Salambo", "user1"));
  }

  @Test
  void returnWrongUser() {
    libManager.addBook("Madame Bovary", 1);
    when(userService.isUserActive(any())).thenReturn(true);
    libManager.borrowBook("Madame Bovary", "user1");
    assertFalse(libManager.returnBook("Madame Bovary", "user2"));
  }


  @Test
  void thrownException() {
    assertThrows(IllegalArgumentException.class, () -> libManager.calculateDynamicLateFee(-1, true, true));
  }

  @ParameterizedTest
  @CsvSource({
      "6.0, 10, true, true",
      "0.5, 1, false, false",
      "75.0, 100, true, false",
      "40.0, 100, false, true",
      "0.0, 0, true, true"
  })
  void calculateDiscount(
      double expected,
      int overdueDays,
      boolean isBestseller,
      boolean isPremiumMember
  ) {
    assertEquals(expected, libManager.calculateDynamicLateFee(overdueDays, isBestseller, isPremiumMember));
  }
}
