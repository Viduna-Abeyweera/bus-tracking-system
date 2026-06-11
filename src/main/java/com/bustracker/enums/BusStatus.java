package com.bustracker.enums;

/**
 * Enumeration of possible bus operational statuses.
 *
 * <ul>
 *   <li>{@code ACTIVE} — bus is currently on its route, tracking GPS</li>
 *   <li>{@code IDLE} — bus is at the depot or parked, not operating</li>
 *   <li>{@code MAINTENANCE} — bus is under repair, not available</li>
 * </ul>
 */
public enum BusStatus {
    ACTIVE,
    IDLE,
    MAINTENANCE
}
