import Foundation

// This file provides Swift's Task functionality without importing `shared`
// which would shadow Swift's Task with Kotlin's Task type

enum SwiftConcurrency {
    @discardableResult
    static func run<T>(_ operation: @escaping () async -> T) -> Task<T, Never> {
        Task {
            await operation()
        }
    }
    
    @discardableResult
    static func runThrowing<T>(_ operation: @escaping () async throws -> T) -> Task<T, Error> {
        Task {
            try await operation()
        }
    }
    
    static var isCancelled: Bool {
        Task<Never, Never>.isCancelled
    }
    
    static func sleep(nanoseconds: UInt64) async throws {
        try await Task<Never, Never>.sleep(nanoseconds: nanoseconds)
    }
}

// Typealias for Swift's Task type that won't be shadowed
typealias ConcurrentTask<Success, Failure: Error> = Task<Success, Failure>
typealias ConcurrentTaskNever<Success> = Task<Success, Never>

