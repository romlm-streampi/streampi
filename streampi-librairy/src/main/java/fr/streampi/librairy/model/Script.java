package fr.streampi.librairy.model;

import java.io.IOException;

@FunctionalInterface
public interface Script {
	void execute() throws IOException;
}
