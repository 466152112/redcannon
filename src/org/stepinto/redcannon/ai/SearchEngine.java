package org.stepinto.redcannon.ai;

import java.util.*;
import org.stepinto.redcannon.common.*;

public class SearchEngine {
	private Evaluator[] evaluators;
	private Selector[] selectors;
	private Validator[] validators;
	
	public void addEvvaluator(Evaluator e) {
		evaluators = Arrays.copyOf(evaluators, evaluators.length+1);
		evaluators[evaluators.length-1] = e;
	}
	
	public void addSelector(Selector s) {
		selectors = Arrays.copyOf(selectors, selectors.length+1);
		selectors[selectors.length-1] = s;
	}
	
	public void addValidator(Validator v) {
		validators = Arrays.copyOf(validators, validators.length+1);
		validators[validators.length-1] = v;
	}
}
