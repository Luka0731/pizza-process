package ch.frox.pizzaprocess.main.java.core.security;

import ch.ivyteam.ivy.security.IUser;



public record GuestAccount(IUser user, String password) {
}