/**
 * Copyright 2013 AppDynamics
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appdynamics.alerts.twitter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Stephen.Dong
 * Date: 12/3/13
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class HealthRuleViolation extends Event{
    String pvnAlertTime;
    String healthRuleName;
    String healthRuleID;
    String pvnTimePeriodInMinutes;
    String affectedEntityType;
    String affectedEntityName;
    String affectedEntityID;
    List<EvaluationEntity> evaluationEntity;
    String summaryMessage;
    String incidentID;
    String eventType;

    public class EvaluationEntity {
        String type;
        String name;
        String id;
        List<String> triggeredCondition;
    }
}
