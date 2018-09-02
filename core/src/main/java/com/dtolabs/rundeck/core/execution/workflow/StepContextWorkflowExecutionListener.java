/*
 * Copyright 2016 SimplifyOps, Inc. (http://simplifyops.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtolabs.rundeck.core.execution.workflow;

import com.dtolabs.rundeck.core.utils.NullablePairImpl;
import com.dtolabs.rundeck.core.utils.Pair;
import com.dtolabs.rundeck.core.utils.PairImpl;
import com.dtolabs.rundeck.core.utils.Pairs;

import java.util.ArrayList;
import java.util.List;

/**
 * Listens to step and node context changes, and maintains thread-local step+node context
 */
public class StepContextWorkflowExecutionListener<NODE, STEP> implements StepNodeContextListener<NODE, STEP>,
        StepNodeContext<NODE, STEP> {

    public class ctxPair extends NullablePairImpl<STEP, NODE> implements Pair<STEP,NODE> {
        ctxPair(STEP first, NODE second) {
            super(first, second);
        }
    }
    /**
     * Thread local context stack, inherited by sub threads.
     */
    private InheritableThreadLocal<STEP> localStep = new InheritableThreadLocal<>();
    private InheritableThreadLocal<NODE> localNode = new InheritableThreadLocal<>();
    private InheritableThreadLocal<ContextStack<ctxPair>> contextStack = new
            InheritableThreadLocal<>();

    public void beginContext() {
        STEP info = localStep.get();
        NODE node = localNode.get();
        if (null != info) {
            //within another workflow already, so push context onto stack
            ctxPair pair = new ctxPair(info, node);
            if (null != contextStack.get()) {
                contextStack.set(contextStack.get().copyPush(pair));
            } else {
                contextStack.set(ContextStack.create(pair));
            }
        }
        localStep.set(null);
        localNode.set(null);
    }

    public void finishContext() {
        ContextStack<ctxPair> stack = contextStack.get();
        if (null != stack) {
            //pop any workflow context already on stack
            if (stack.size() > 0) {
                ctxPair pop = stack.pop();
                localStep.set(pop.getFirst());
                localNode.set(pop.getSecond());
            } else {
                localStep.set(null);
                localNode.set(null);
                contextStack.set(null);
            }
        }else{
            localStep.set(null);
            localNode.set(null);
        }
    }

    public void beginStepContext(STEP step) {
        localStep.set(step);
    }

    public void finishStepContext() {
        localStep.set(null);
    }

    public void beginNodeContext(NODE node) {
        localNode.set(node);
    }

    public void finishNodeContext() {
        localNode.set(null);
    }


    public STEP getCurrentStep() {
        return localStep.get();
    }
    public NODE getCurrentNode() {
        return localNode.get();
    }

    public List<STEP> getCurrentContext() {
        List<Pair<STEP,NODE>> currentContextPairs = getCurrentContextPairs();
        if (null != currentContextPairs) {
            return Pairs.listFirst(currentContextPairs);
        }
        return null;
    }

    public List<Pair<STEP,NODE>> getCurrentContextPairs() {
        STEP step = localStep.get();
        NODE node = localNode.get();
        if (null != step) {
            if (null != contextStack.get()) {
                return getPairs(contextStack.get().copyPush(new ctxPair(step, node)).stack());
            } else {
                return getPairs(ContextStack.create(new ctxPair(step, node)).stack());
            }
        } else if (null != contextStack.get()) {
            List<Pair<STEP, NODE>> stack = getPairs(contextStack.get().stack());
            if (stack.size() > 0) {
                return stack;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private List<Pair<STEP, NODE>> getPairs(List<ctxPair> stack) {
        ArrayList<Pair<STEP, NODE>> pairs = new ArrayList<>();
        for (ctxPair ctxPair : stack) {
            pairs.add(ctxPair);
        }
        return pairs;
    }
}
