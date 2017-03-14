package ta;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

import com.google.common.base.Preconditions;

import ta.declarations.ClockDecl;
import ta.declarations.VariableDecl;
import ta.parser.TALexer;
import ta.parser.TAParser;
import ta.state.State;
import ta.transition.Transition;
import ta.visitors.TAVisitor;

public class TA {

	private final Set<VariableDecl> variableDeclaration;
	
	private final Set<ClockDecl> clockDeclarations;
	
	/**
	 * The name of the timed automaton
	 */
	private final String identifier;

	/**
	 * The initial state of the timed automaton
	 */
	private final State initialState;

	/**
	 * The atomic propositions of the timed automaton
	 */
	private final Set<AP> atomicPropositions;

	/**
	 * The states of the timed automaton
	 */
	private final Set<State> states;

	private final Set<Clock> clocks;
	
	private final Set<Variable> variables;
	/**
	 * The transitions of the timed automaton
	 */
	private final Set<Transition> transitions;

	/**
	 * keeps the outgoint transitions for each state
	 */
	private final Map<State, Set<Transition>> outTransitions;

	public TA(String identifier, Set<AP> atomicPropositions, Set<State> states, Set<Transition> transitions,
			State initialState, Set<Clock> clocks, Set<Variable> variables, Set<VariableDecl> variableDeclaration, Set<ClockDecl> clockDeclarations) {
		Preconditions.checkNotNull(states, "The set of the states cannot be null");
		Preconditions.checkNotNull(clocks, "The set of the clocks cannot be null");
		Preconditions.checkNotNull(variables, "The set of the variables cannot be null");

		this.identifier = identifier;
		if (atomicPropositions != null) {
			this.atomicPropositions = new HashSet<>(atomicPropositions);
		} else {
			this.atomicPropositions = new HashSet<>();
		}
		this.states = new HashSet<>(states);
		this.transitions = new HashSet<>(transitions);
		this.initialState = initialState;
		this.clocks = clocks;
		this.outTransitions = new HashMap<>();
		this.variables=variables;

		this.states.forEach(s -> this.outTransitions.put(s, new HashSet<>()));
		this.transitions.forEach(t -> {
			Preconditions.checkArgument(this.outTransitions.containsKey(t.getSource()),
					"The state " + t.getSource() + "is not contained in the states of the TA");
			
			this.outTransitions.get(t.getSource()).add(t);
		});
		
		this.variableDeclaration=variableDeclaration;
		this.clockDeclarations=clockDeclarations;
	}

	public Set<Variable> getVariables() {
		return variables;
	}

	public Set<Transition> getOutgoingTransitions(State state) {
		Preconditions.checkNotNull(state, "The state cannot be null");
		return this.outTransitions.get(state);
	}

	public <T> T accept(TAVisitor<T> visitor) {
		return visitor.visit(this);
	}

	public Set<AP> getAtomicPropositions() {
		return Collections.unmodifiableSet(atomicPropositions);
	}

	public Set<State> getStates() {
		return Collections.unmodifiableSet(states);
	}

	public Set<Transition> getTransitions() {
		return Collections.unmodifiableSet(transitions);
	}

	public State getInitialState() {
		return initialState;
	}

	public String getIdentifier() {
		return identifier;
	}

	/**
	 * returns the set of the clocks of the TA
	 * 
	 * @return the set of the clocks of the TA
	 */
	public Set<Clock> getClocks() {

		return Collections.unmodifiableSet(clocks);
	}

	public Set<VariableDecl>  getDeclarations(){
		return this.variableDeclaration;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		
		StringBuilder builder=new StringBuilder();
		builder.append( "-----------------------------TIMED AUTOMATON----------------------------- \n");
		
		builder.append( "TA \n");
		builder.append("id: \n");
		builder.append("\t"+this.identifier+"\n");
		builder.append("clocks: \n");
		builder.append("\t");
		this.clocks.forEach(c -> builder.append(c+",\t"));
		builder.append("\n");
		builder.append("variables: \n");
		builder.append("\t");
		this.variables.forEach(c -> builder.append(c+",\t"));
		builder.append("\n");
		builder.append( "initialstate \n");
		builder.append("\t"+this.getInitialState()+"\n");
		builder.append( "states \n");
		this.states.forEach(s -> builder.append("\t"+s+",\n"));
		builder.append( "transitions \n");
		this.transitions.forEach(t -> builder.append("\t"+t+",\n"));
		builder.append( "--------------------------\n");
		builder.append( "DECLARATION \n");
		builder.append( "variables \n");
		this.variableDeclaration.forEach(v -> builder.append("\t"+v+",\n"));
		builder.append( "clocks \n");
		
		this.clockDeclarations.forEach(v -> builder.append("\t"+v+",\n"));
		
		builder.append( "-------------------------------------------------------------------------------- \n");
		return builder.toString();
	}

	public static TA parse(String filePath) throws IOException {
		ANTLRInputStream input = new ANTLRFileStream(ClassLoader.getSystemResource(filePath).getPath());
		TALexer lexer = new TALexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		TAParser parser = new TAParser(tokens);
		parser.setBuildParseTree(true);
		SystemDecl system = parser.ta().systemret;

		TA ta = system.getTimedAutomata().iterator().next();
		return ta;
	}
}
