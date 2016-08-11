package testing.agent;

import java.lang.instrument.Instrumentation;

import testing.instrument.transformer.MyClassTransformer;

public class MyAgentBeforeMain {

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("-- MyAgentBeforeMain is alive!!!");
		inst.addTransformer(new MyClassTransformer());
	}

}
