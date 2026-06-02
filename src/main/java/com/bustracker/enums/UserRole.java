package com.bustracker.enums;

/**
 * Enumeration of user roles in the Bus Tracking System.
 *
 * <p>Roles define the level of access a user has:</p>
 * <ul>
 *   <li>{@code PASSENGER} — can view bus locations, ETA, and bus stops</li>
 *   <li>{@code DRIVER} — can submit GPS locations and view their assigned routes</li>
 *   <li>{@code ADMIN} — full system access: manage users, buses, routes, and stops</li>
 * </ul>
 *
 * <p>This enum is used with Spring Security to enforce role-based access
 * control on API endpoints.</p>
 */
public enum UserRole {
    PASSENGER,
    DRIVER,
    ADMIN
}
