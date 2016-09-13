/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.raft.examples.arithmatic;

import org.apache.raft.examples.arithmatic.expression.Expression;
import org.apache.raft.examples.arithmatic.expression.Variable;
import org.apache.raft.protocol.Message;

import java.nio.charset.Charset;
import java.util.Map;

public class AssignmentMessage implements Message, Evaluable {
  public static final Charset UTF8 = Charset.forName("UTF-8");

  private final Variable variable;
  private final Expression expression;

  public AssignmentMessage(Variable variable, Expression expression) {
    this.variable = variable;
    this.expression = expression;
  }

  public AssignmentMessage(byte[] buf, int offset) {
    variable = new Variable(buf, offset);
    expression = Expression.Utils.bytes2Expression(buf, offset + variable.length());
  }

  public AssignmentMessage(Message message) {
    this(message.getContent(), 0);
  }

  public Variable getVariable() {
    return variable;
  }

  public Expression getExpression() {
    return expression;
  }

  @Override
  public byte[] getContent() {
    final int length = variable.length() + expression.length();
    final byte[] bytes = new byte[length];
    final int offset = variable.toBytes(bytes, 0);
    expression.toBytes(bytes, offset);
    return bytes;
  }

  @Override
  public String toString() {
    return variable + " = " + expression;
  }

  @Override
  public Double evaluate(Map<String, Double> variableMap) {
    final Double value = expression.evaluate(variableMap);
    final String name = variable.getName();
    if (value == null) {
      variableMap.remove(name);
    } else {
      variableMap.put(name, value);
    }
    return value;
  }
}