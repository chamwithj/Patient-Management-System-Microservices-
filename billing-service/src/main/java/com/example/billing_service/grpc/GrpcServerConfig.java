package com.example.billing_service.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class GrpcServerConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server grpcServer(BillingGrpcService billingGrpcService) throws IOException {
        return ServerBuilder
                .forPort(9001)
                .addService(billingGrpcService)
                .build();
    }
}
