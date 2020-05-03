package fr.streampi.server.io.model;

import java.net.InetAddress;

public class Client {

	private InetAddress address;
	private ClientProcessor processor;
	private DataClientProcessor dataProcessor;

	public Client() {
		super();
	}

	public Client(InetAddress address, ClientProcessor processor, DataClientProcessor dataProcessor) {
		super();
		this.address = address;
		this.processor = processor;
		this.dataProcessor = dataProcessor;
	}

	/**
	 * @return the address
	 */
	public final InetAddress getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public final void setAddress(InetAddress address) {
		this.address = address;
	}

	/**
	 * @return the processor
	 */
	public final ClientProcessor getProcessor() {
		return processor;
	}

	/**
	 * @param processor the processor to set
	 */
	public final void setProcessor(ClientProcessor processor) {
		this.processor = processor;
	}

	/**
	 * @return the dataProcessor
	 */
	public final DataClientProcessor getDataProcessor() {
		return dataProcessor;
	}

	/**
	 * @param dataProcessor the dataProcessor to set
	 */
	public final void setDataProcessor(DataClientProcessor dataProcessor) {
		this.dataProcessor = dataProcessor;
	}

	@Override
	public String toString() {
		return "Client [address=" + address + ", processor=" + processor + ", dataProcessor=" + dataProcessor + "]";
	}

}
