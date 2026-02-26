package ru.hh.school.unittesting.homework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LibraryManagerTest{

  //реализация сервиса уведомлений с одним методом который просто вывод id юзера и сообщение
  class NotificationServiceImpl implements NotificationService{
    public void notifyUser(String userId, String message){
      System.out.println("User: "+userId+"\tMessage: "+message);
    }
  };
  //реализация юзер сервиса в котором один метод, проверяющий активен ли пользователь
  class UserServiceImpl implements UserService{
    public boolean isUserActive(String userId){
      return true;
    }
  }

  NotificationService notificationService = new NotificationServiceImpl();
  UserService userService = new UserServiceImpl();

  @Test
  void addingBooks(){
    LibraryManager libManager = new LibraryManager(notificationService, userService);

    //добавили 10 книг, проверяем что корректно работает уменьшение при взятии книги
    libManager.addBook("978-5-699-12014-7", 10);

    for (int i = 0; i < 10; i++) {
      assertTrue(libManager.borrowBook("978-5-699-12014-7", "8be4df61−93ca−11d2−aa0d−00e098032b8c"));
    }
    assertFalse(libManager.borrowBook("978-5-699-12014-7", "8be4df61−93ca−11d2−aa0d−00e098032b8c"));
  }

  @Test
  void getBooks(){
    LibraryManager libManager = new LibraryManager(notificationService, userService);

    //добавили 99 книг, проверяем что корректно работает получение колва книг
    libManager.addBook("978-5-699-12014-7", 99);
    assertEquals(99, libManager.getAvailableCopies("978-5-699-12014-7"));
  }

  @Test
  void returnBooks(){
    LibraryManager libManager = new LibraryManager(notificationService, userService);

    //проверяем возврат взятых книг
    libManager.addBook("978-5-699-12014-7", 1);

    //взяли книгу
    libManager.borrowBook("978-5-699-12014-7", "8be4df61−93ca−11d2−aa0d−00e098032b8c");
    assertEquals(0, libManager.getAvailableCopies("978-5-699-12014-7"));

    //вернули книгу
    assertTrue(libManager.returnBook("978-5-699-12014-7", "8be4df61−93ca−11d2−aa0d−00e098032b8c"));
    assertEquals(1, libManager.getAvailableCopies("978-5-699-12014-7"));
  }

  @Test
  void calculateDiscount(){
    LibraryManager libManager = new LibraryManager(notificationService, userService);

    assertEquals(6.0, libManager.calculateDynamicLateFee(10,true,true));
    assertEquals(0.5, libManager.calculateDynamicLateFee(1, false, false));
    assertEquals(75.0 ,libManager.calculateDynamicLateFee(100, true, false));
    assertEquals(40.0 ,libManager.calculateDynamicLateFee(100, false, true));
  }

}