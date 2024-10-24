# Distributed File System Project

Welcome to the **Distributed File System** project! This project is aimed at creating a basic distributed file system that supports adding, editing, and deleting text files across multiple nodes. The system will work in a **master-chunk architecture**, where a master server coordinates the distribution and replication of file chunks to multiple chunk servers.

This project is created for **learning purposes** and distributed under MIT License, allowing developers to explore the concepts of distributed systems, RESTful applications, and containerized deployments using Docker.

The file system will be built using **Java** as a **RESTful application** and will run inside **Docker containers**. This README introduces the structure and current status of the system. The project is still a work in progress, with several functionalities under development.

## Features

### File Operations
- [ ] **Add, Edit, Delete Files**: The system will allow basic file operations. These functionalities are yet to be implemented.
- [ ] **Text File Support Only**: As a simplification, the system currently only supports operations on text files.

### Application Design
- [x] **REST Application**: The system will be exposed via REST endpoints. The basic structure for this is already in place, but file operation functionality still needs to be implemented.
- [x] **Dockerized Environment**: The system runs inside Docker containers. This is fully ready and operational.

## System Architecture

### Modes of Operation
The system will have two modes of operation:
- **Master Mode**
- **Chunk Mode**

### Master Server Responsibilities
The master server coordinates file distribution, replication, and chunk management across chunk servers. The following are the responsibilities of the master:

- [x] **Heartbeat Monitoring**: The master tracks the heartbeat of all chunk servers to ensure they are operational.
- [ ] **File Distribution**: When a client uploads a file, the master will split the file into chunks (based on chunk size) and distribute each chunk to at least 3 chunk servers as replicas. This feature is yet to be implemented.
- [ ] **Chunk Tracking**: The master will maintain metadata about which file chunks are stored on which chunk servers.
- [ ] **Replica Recovery**: If a chunk server goes down, and the number of replicas for a chunk falls below the required minimum, the master will copy the chunk from other available replicas to another server. Yet to be implemented.
- [ ] **File Access Links**: When a client requests a file, the master will provide links to the chunk servers holding the required chunks.
- [ ] **Chunk Deletion on File Removal**: If a file is deleted by the client, the master will instruct all chunk servers to remove the chunks of that file.
- [ ] **Replica Management**: The master will manage replication levels and instruct chunk servers to delete excess replicas if the number exceeds the required count.
- [ ] **Client Handling**: When a client requests a file, Master will go through the metadata of the file, find corresponding chunk servers and send back the list of chunk servers to the client.

### Chunk Server Responsibilities
The chunk server's primary role is to store and manage file chunks. The following are its responsibilities:

- [x] **Heartbeat**: The chunk server sends regular heartbeat signals to the master.
- [ ] **Store Chunks**: The server will store file chunks along with their metadata.
- [ ] **Serve File Chunks**: Upon client requests, the chunk server will send the appropriate file chunks to the client.
- [ ] **Store/Delete Chunks on Master’s Instruction**: The server will store or delete file chunks as instructed by the master server.

### Client Responsibilities
The client interacts with the system for file uploads and downloads. The following are the client's responsibilities:

- [ ] **Upload/Download Files**: The client can upload a file, which will be split into chunks and distributed. The client can also download files, receiving the chunks from various servers.
- [ ] **File Merging**: When downloading a file, the client will receive the file in chunks. It is the client's responsibility to merge the chunks back into the correct order as specified by the master server.

## Getting Started

1. **Run the Master and Chunk Servers**:
    - The master and chunk servers are run inside Docker containers.
    - Ensure Docker is installed and running.

2. **REST API Endpoints**:
    - The system exposes various REST endpoints for file operations (yet to be implemented).

3. **Client Operations**:
    - A client can interact with the system by uploading or downloading files through the REST API.

## To-Do List

- [ ] Implement file upload, edit, and delete functionalities.
- [ ] Develop chunk distribution and replication strategies.
- [ ] Implement chunk storage and retrieval mechanisms.
- [ ] Implement replication recovery and replica management.
- [ ] Ensure proper chunk deletion when a file is removed.
- [ ] Finalize client-side file merging.

## Future Enhancements
- Support for non-text file types.
- Improve fault tolerance with additional replicas and redundancy mechanisms.
- Add logging and monitoring for better error tracking.

---

This README outlines the current state of the distributed file system project. As the system is still under development, many features are yet to be completed, but the fundamental architecture is in place. This is a **learning project**, providing hands-on experience with distributed systems and related technologies.
