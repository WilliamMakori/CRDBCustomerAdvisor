package com.crdb.advisor;

import com.amazonaws.services.lambda.runtime.ClientContext;
import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class LocalContext implements Context {

    @Override
    public String getAwsRequestId() { return "local-request"; }

    @Override
    public String getLogGroupName() { return "local"; }

    @Override
    public String getLogStreamName() { return "local"; }

    @Override
    public String getFunctionName() { return "local"; }

    @Override
    public String getFunctionVersion() { return "local"; }

    @Override
    public String getInvokedFunctionArn() { return "local"; }

    @Override
    public CognitoIdentity getIdentity() { return null; }

    @Override
    public ClientContext getClientContext() { return null; }

    @Override
    public int getRemainingTimeInMillis() { return 30000; }

    @Override
    public int getMemoryLimitInMB() { return 512; }

    @Override
    
public LambdaLogger getLogger() {
    return new LambdaLogger() {
        @Override
        public void log(String message) {
            System.out.println("[LOG] " + message);
        }

        @Override
        public void log(byte[] message) {
            System.out.println("[LOG] " + new String(message));
        }
    };
}
}