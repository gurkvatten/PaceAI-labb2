package com.paceai.user.grpc

import com.paceai.user.repository.UserRepository
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class UserGrpcService(
    private val userRepository: UserRepository
) : UserServiceGrpc.UserServiceImplBase() {

    override fun getUser(
        request: UserProto.GetUserRequest,
        responseObserver: StreamObserver<UserProto.UserResponse>
    ) {
        val user = userRepository.findById(request.userId).orElse(null)

        val response = if (user != null) {
            UserProto.UserResponse.newBuilder()
                .setId(user.id)
                .setUsername(user.username)
                .setEmail(user.email)
                .build()
        } else {
            UserProto.UserResponse.newBuilder().build()
        }

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}