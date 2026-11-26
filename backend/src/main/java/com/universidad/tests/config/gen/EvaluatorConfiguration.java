package com.universidad.tests.config.gen;

import com.universidad.tests.application.evaluator.TestEvaluatorRegistry;
import com.universidad.tests.application.evaluator.bfa.BfaTestEvaluator;
import com.universidad.tests.application.evaluator.bfa.ConsolidadorCompuestosBfa;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EvaluatorConfiguration {

    @Bean
    public ConsolidadorCompuestosBfa consolidadorCompuestosBfa() {
        return new ConsolidadorCompuestosBfa();
    }

    @Bean
    public BfaTestEvaluator bfaTestEvaluator(ConsolidadorCompuestosBfa consolidador) {
        return new BfaTestEvaluator(consolidador);
    }

    @Bean
    public TestEvaluatorRegistry testEvaluatorRegistry(BfaTestEvaluator bfaTestEvaluator) {
        TestEvaluatorRegistry registry = new TestEvaluatorRegistry();
        registry.registrar(bfaTestEvaluator);
        return registry;
    }
}
