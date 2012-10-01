package ejava.examples.jmsmechanics;

public interface JMSAdmin {

	public abstract JMSAdmin deployTopic(String name, String jndiName)
			throws Exception;

	public abstract JMSAdmin deployQueue(String name, String jndiName)
			throws Exception;

	public abstract JMSAdmin destroyTopic(String name) throws Exception;

	public abstract JMSAdmin destroyQueue(String name) throws Exception;

	public abstract void close() throws Exception;
}