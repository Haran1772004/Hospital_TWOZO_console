package com.hospital;

import com.hospital.controller.AuthMenu;
import java.util.Scanner;

public class App {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    AuthMenu.start(scanner);
    scanner.close();
    System.out.println("Exiting. Goodbye!");
  }
}
