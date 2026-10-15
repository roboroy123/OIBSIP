# Online Reservation System

> OIBSIP Internship · Java Development · Task 1

A GUI-based train reservation system built with **Java Swing**, **JDBC** and **SQLite**.
Users log in, book tickets (a unique PNR is generated), and cancel bookings using the PNR.

## Features
- Login form with credential check ("Access denied" on invalid login)
- Reservation form: passenger name, train number, auto-populated train name, class type, date of journey, source and destination
- Booking saved to SQLite with an auto-generated unique 10-digit PNR, followed by a confirmation dialog
- Cancellation: enter PNR, **Fetch** shows full details, **Confirm Cancellation** asks "Are you sure?" and deletes the booking
- Input validation: no empty required fields, strict `yyyy-MM-dd` date (not in the past), numeric train number, source ≠ destination
- All SQL uses `PreparedStatement` (SQL-injection safe); passwords stored as SHA-256 hashes

## Tech Stack
Java 17 · Swing · JDBC · SQLite (`org.xerial:sqlite-jdbc`) · Maven

## Project Structure
```
src/main/java/com/reservation/
├── Main.java
├── db/Database.java            # connection, schema, seed data
├── dao/                        # UserDao, TrainDao, ReservationDao
├── model/                      # Reservation, Options
├── util/PasswordUtil.java
└── ui/                         # LoginFrame, MainFrame, ReservationPanel, CancellationPanel
```

## Run
Requires JDK 17+ and Maven.
```bash
mvn compile exec:java -Dexec.mainClass=com.reservation.Main
# or build a runnable jar
mvn clean package
java -jar target/online-reservation-system-1.0.0.jar
```
The SQLite file `reservation.db` is created automatically in the working directory on first run.

## Demo Login
| Username | Password |
|----------|----------|
| admin    | admin123 |

## Sample Train Numbers
12621 (Tamil Nadu Express), 12007 (Shatabdi Express), 12840 (Howrah Mail), 12163, 12601, 12649, 12695, 22625

## Database Schema
- `users(username, password_hash)`
- `trains(train_number, train_name)`
- `reservations(pnr, passenger_name, train_number, train_name, class_type, journey_date, source, destination)`

## Screenshots
Add your images to the `screenshots/` folder and keep these names:

| Screen | File |
|--------|------|
| Login | `screenshots/01-login.png` |
| Booking form | `screenshots/02-booking.png` |
| Booking confirmation | `screenshots/03-confirmation.png` |
| Cancellation (fetched details) | `screenshots/04-cancellation.png` |

![Login](screenshots/01-login.png)
![Booking](screenshots/02-booking.png)
![Confirmation](screenshots/03-confirmation.png)
![Cancellation](screenshots/04-cancellation.png)

## Demo Video
LinkedIn post: _paste link here_
