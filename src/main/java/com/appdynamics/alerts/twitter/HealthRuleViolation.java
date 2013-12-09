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
