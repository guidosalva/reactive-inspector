package de.tu_darmstadt.stg.reclipse.graphview.model;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;

import de.tu_darmstadt.stg.reclipse.graphview.model.SerializationEventLogger.NodeEvent;

public class NodeEventIterator implements Iterator<NodeEvent> {

	private final ObjectInputStream in;

	private NodeEvent next;

	public NodeEventIterator(String sessionName) throws IOException {
		this.in = new ObjectInputStream(new BufferedInputStream(
				getInputStream(sessionName)));
		readNext();
	}

	private InputStream getInputStream(String sessionName) throws FileNotFoundException {
		File file = new File("sessions/" + sessionName + ".ser");
		return new FileInputStream(file);
	}

	private void readNext() {
		try {
			next = (NodeEvent) in.readObject();
		} catch (EOFException e) {
			next = null;
		} catch (ClassNotFoundException | IOException e) {
			next = null;
			e.printStackTrace();
		}
	}

	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public boolean hasNext() {
		return next != null;
	}

	@Override
	public NodeEvent next() {
		NodeEvent event = next;
		readNext();
		return event;
	}

}
