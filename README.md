# Kanban-board: Collaborative Real-Time Kanban Board with Distributed State Synchronization
This is the project implementation of the master modul: Principles, Algorithms and Models of Concurrency and Synchronization 

Project Meta Information:

* **Course:** Principles, Algorithms and Models of Concurrency and Synchronization
* **Group Size:** 2 Students (Anna-Livia Martin, David Rossel)
* **Submission Date:** June 23, 2026
* **Target Completion:** July 31, 2026

## 1. Project Overview & Motivation

The goal of this project is to design and implement a collaborative, real-time Kanban board system. Traditional web applications rely on standard sequential CRUD (Create, Read, Update, Delete) patterns over HTTP, where state updates are pulled by the client. In contrast, this project explores the challenges of multi-user concurrency where state changes are driven asynchronously in real-time.

When multiple users interact with the same board simultaneously, concurrent modifications to tasks, column constraints, and state transitions inevitably lead to data hazards. This project focuses on solving these synchronization issues on the server side using core concurrency models and algorithms, shifting the complexity from standard database constraints into **in-memory thread synchronization** and **event-driven communication**.

## 2. System Architecture & Tech Stack

The architecture follows a strict separation of concerns, optimized for real-time message distribution:

* **Backend (Spring Boot / Java):** The server acts as the centralized authority holding the active system state. It handles incoming asynchronous WebSocket connections, manages thread pools for request processing, and executes safety-critical synchronization code.
* **Frontend (Vue / TypeScript):** A reactive single-page application providing a visual representation of the board. It connects via persistent WebSockets (STOMP protocol) to receive immediate state broadcasts and push user actions.

## 3. Concurrency Challenges & Applied Concepts

The system applies the following core concurrency concepts covered in the lecture:

* **Thread Pools & Asynchronous Event Handling:** Spring Boot’s underlying framework handles incoming WebSocket sessions concurrently. When an event is triggered, a worker thread handles the processing, decoupling network I/O from core business logic.
* **Mutual Exclusion & Critical Sections (Locks):** Operations such as moving a card require structural checks.
* **Preventing "Check-Then-Act" Faults (WIP Limits):** Kanban boards implement Work-in-Progress (WIP) limits (e.g., maximum 3 cards in "Testing"). If two users concurrently drag a card into the same column, a classic race condition occurs.
* **Data Consistency Models (Optimistic/Pessimistic Locking):** To prevent Lost Updates (e.g., two users editing the description of the same card concurrently), we implement a version-based optimistic concurrency control or a transient lock mechanism that notifies other users in real-time when a card is "occupied".

## 4. Scope & Minimum Viable Product (MVP)

To ensure feasibility within the 5-week timeframe, the scope is kept lean and focused:

- **Single Kanban Board:** A globally accessible board with creatable and orderable columns
- **Real-Time Collaboration:** Ability to create, edit, and drag-and-drop tasks with instantaneous updates across clients.
- **Thread-Safe WIP Limits:** Server-side enforced Work-in-Progress limits verified via thread-safe mechanisms.
- **Conflict Resolution UI:** Visual indicators in the frontend (e.g., immediate rollback or warning notification if an action was rejected due to a concurrency conflict).

## 5. Features
These are the features of the kanban board.

### 5.1 Board Creation

#### User Story

As a user, I want to create a Kanban board so that I can organize tasks and workflows within a shared workspace.

#### Description

The system shall allow users to create a new Kanban board by providing a board name. A board represents the top-level container of the application and contains all columns and tasks related to a project. After creation, the board is stored on the server and immediately available to all connected clients.

The backend exposes a REST endpoint for board creation and persists the board in the database. Once the board is created, a corresponding event can be distributed to connected clients to keep all application states synchronized.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Board creation dialog
* Form validation
* API integration via Axios
* Reactive state update using Pinia

**Backend (Spring Boot)**

* REST Controller for board creation
* Form & Entity validation
* Service layer for validation and business logic
* JPA repository for persistence
* STOMP

### 5.2 Column Creation

#### User Story

As a user, I want to create columns within a board so that I can structure my workflow into different stages.

#### Description

Users can create custom workflow columns such as *To Do*, *In Progress*, *Testing*, or *Done*. Each column can optionally define a Work-In-Progress (WIP) limit that restricts the number of tasks allowed within that column.

New columns are instantly synchronized with all connected users through the WebSocket infrastructure. One can only move or edit the column if no task in the column is currently edited or moved.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Column creation dialog
* Dynamic board rendering
* Real-time updates through WebSocket subscriptions
* Form validation
* Pinia Store

**Backend (Spring Boot)**

* REST endpoint for column creation
* Validation of column names and WIP limits
* Database persistence
* STOMP/WebSocket event broadcasting
* Form & Entity validation

### 5.3 Task Creation

#### User Story

As a user, I want to create tasks so that I can track work items on the Kanban board.

#### Description

A task represents a unit of work and contains information such as title, description, creation date, and current workflow state.

Users can create tasks directly within a selected column. The backend validates the request and ensures that the target column does not violate any configured WIP restrictions.

After successful creation, all connected clients receive the new task immediately via WebSocket events.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Task creation modal
* Form validation
* Real-time rendering
* Pinia store

**Backend (Spring Boot)**

* REST API for task creation
* Validation logic
* Database persistence
* Real-time event publication
* Form & Entity validation

### 5.4 Task Movement and Drag-and-Drop

#### User Story

As a user, I want to move tasks between columns using drag-and-drop so that I can update the progress of work items.

#### Description

Tasks can be moved between workflow stages through a drag-and-drop interface. When a task is moved, the frontend sends a request to the backend, which validates the operation before updating the shared board state.

Successful moves are broadcast to all connected clients. Invalid moves, such as violations of WIP limits, are rejected and immediately communicated back to the initiating user. Only one user can drag and drop a task at the same time. It is then blocked for all other users

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Drag-and-drop functionality using Vue Draggable
* Optimistic UI updates
* Rollback mechanism for rejected operations

**Backend (Spring Boot)**

* Move task endpoint
* Position recalculation
* Synchronization validation
* Event broadcasting


### 5.5 Thread-Safe Work-In-Progress Limits

#### User Story

As a project manager, I want WIP limits to be enforced consistently so that workflow constraints remain valid even during concurrent modifications.

#### Description

Each column may define a maximum number of tasks that are allowed within it. When multiple users attempt to move tasks into the same column simultaneously, race conditions may occur.

To prevent inconsistent board states, the server enforces WIP limits within synchronized critical sections. Only one thread may validate and update a column's task count at a time.

If a move operation would exceed the configured limit, the action is rejected and the user receives immediate feedback.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Error notifications
* Automatic rollback

**Backend (Spring Boot)**

* ReentrantLock-based synchronization
* Critical sections for task movement
* Thread-safe WIP validation
* Concurrency testing with ExecutorService

### 5.6 Real-Time Collaboration

#### User Story

As a user, I want to see updates from other users immediately so that everyone always works on the latest board state.

#### Description

The application supports real-time collaboration using persistent WebSocket connections. Whenever a user creates, edits, deletes, or moves a task, the change is immediately propagated to all connected clients.

This eliminates the need for manual page refreshes and ensures a consistent view of the board across all participants.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* STOMP client integration
* Centralized event handling
* Reactive state synchronization

**Backend (Spring Boot)**

* Spring WebSocket
* STOMP messaging protocol
* Topic-based event distribution
* Asynchronous message processing

### 5.7 Conflict Detection and Resolution

#### User Story

As a user, I want to be informed when another user modifies the same task so that conflicting changes do not overwrite each other.

#### Description

Concurrent modifications to the same task may lead to lost updates. The system therefore implements optimistic concurrency control based on version numbers.

Whenever a task is updated, the client submits the currently known version. If the server detects that another update has already been applied, the operation is rejected and the user is notified about the conflict.

This mechanism ensures data consistency while still allowing a high degree of parallelism.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Conflict notifications
* Reload and retry mechanisms
* User feedback dialogs

**Backend (Spring Boot)**

* JPA optimistic locking (`@Version`)
* Conflict detection
* Version validation
* Exception handling for concurrent modifications

### 5.8 User Presence and Session Awareness

#### User Story

As a user, I want to see who is currently viewing the board so that collaboration becomes more transparent.

#### Description

The system tracks active WebSocket sessions and displays currently connected users. Join and leave events are broadcast to all participants.

This feature improves awareness and highlights active collaboration.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Online user list
* Presence indicators

**Backend (Spring Boot)**

* Session management
* Connection tracking
* Presence event broadcasting

### 5.9 Task Editing/Drag-and-Drop Lock

#### User Story

As a user, I want to see when another user is currently editing or drag-and-dropping a task so that conflicting modifications can be avoided.

#### Description

Whenever a user starts editing a task, the task is temporarily locked. Other users can still view the task but cannot modify it until the editing session ends or the lock expires.

The lock state is synchronized in real-time and displayed directly on the task card.

#### Technical Implementation

**Frontend (Vue + TypeScript)**

* Lock indicator on task cards
* Disabled edit controls
* Real-time lock updates

**Backend (Spring Boot)**

* Task lock management
* Automatic lock expiration
* WebSocket event broadcasting
* ConcurrentHashMap for active locks