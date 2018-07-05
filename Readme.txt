INSTRUCTIONS TO RUN THE PROGRAM :
1. Extract the submitted zip file.
2. Config file, java files, launcher and cleanup scripts should be in the current working directory.
3. Before running the launcher script type command chmod +x launcher.sh cleanup.sh
4. Run the launcher script : 
> sh launcher.sh [configFilename] [netId]
5. Run the cleanup script to kill all the processes before running the program again : 
> sh cleanup.sh [configFilename] [netId]
6. The output files [configFilename-nodeId.out] files will be generated in the current working directory.

Project Description
This project consists of three parts.

Part 1
Implement a distributed system consisting of n nodes, numbered 0 to n − 1, arranged in a certain topology. The topology and information about other parameters will be provided in a configuration file. All channels in the system are bidirectional, reliable and satisfy the first-in-first-out (FIFO) property. You can implement a channel using a reliable socket connection (with TCP or SCTP). For each channel, the socket connection should be created at the beginning of the program and should stay intact until the end of the program. All messages between neighboring nodes are exchanged over these connections.
All nodes execute the following protocol:
• Initially, each node in the system is either active or passive. At least one node must be active at the beginning of the protocol.
• While a node is active, it sends anywhere from minPerActive to maxPerActive messages, and then turns passive. For each message, it makes a uniformly random selection of one of its neighbors as the destination. Also, if the node stays active after sending a message, then it waits for at least minSendDelay time units before sending the next message.
• Only an active node can send a message.
• A passive node, on receiving a message, becomes active if it has sent fewer than maxNumber messages (summed over all active intervals). Otherwise, it stays passive.
We refer to the protocol described above as the MAP protocol.

Part 2
Implement the Chandy and Lamport’s protocol for recording a consistent global snapshot as dis- cussed in the class. Assume that the snapshot protocol is always initiated by node 0 and all channels in the topology are bidirectional. Use the snapshot protocol to detect the termination of the MAP protocol described in Part 1. The MAP protocol terminates when all nodes are passive and all channels are empty. To detect termination of the MAP protocol, augment the Chandy and Lamport’s snapshot protocol to collect the information recorded at each node at node 0 using a converge-cast operation over a spanning tree. The tree can be built once in the beginning or on-the-fly for an instance using MARKER messages. Note that, in this project, the messages exchanged by the MAP protocol are application messages and the messages exchanged by the snapshot protocol are control messages. The rules of the MAP protocol (described in Part 1) only apply to application messages. They do not apply to control messages. Testing Correctness of the Snapshot Protocol Implementation To test that your implementation of the Chandy and Lamport’s snapshot protocol is correct, implement Fidge/Mattern’s vector clock protocol described in the class. The vector clock of a node is part of the local state of the node and its value is also recorded whenever a node records its local state. Node 0, on receiving the information recorded by all the nodes, uses these vector timestamps to verify that the snapshot is indeed consistent. Note that only application messages will carry vector timestamps.

Part 3
Design and implement a protocol for bringing all nodes to a halt after node 0 has detected termination of the MAP protocol.