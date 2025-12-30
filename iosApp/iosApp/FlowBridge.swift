import Foundation
import Combine
import shared

// MARK: - ApplicationsViewModel Bridge
class ApplicationsViewModel: ObservableObject {
    @Published var applications: [Application] = []
    @Published var isLoading: Bool = false
    @Published var error: String?
    @Published var filteredApplications: [Application] = []
    @Published var selectedStatus: ApplicationStatus? = nil
    @Published var searchQuery: String = ""
    
    private let viewModel: shared.ApplicationsViewModel
    private var observationTask: ConcurrentTaskNever<Void>?
    
    init() {
        self.viewModel = KoinHelper.shared.getApplicationsViewModel()
        observeUiState()
    }
    
    deinit {
        observationTask?.cancel()
    }
    
    private func observeUiState() {
        // Use a simple polling mechanism to read StateFlow value
        observationTask = SwiftConcurrency.run { [weak self] in
            guard let self = self else { return }
            
            var lastState: ApplicationsUiState? = nil
            
            while !SwiftConcurrency.isCancelled {
                // Get current state using helper function
                let currentState = self.viewModel.getCurrentState()
                
                // Only update if state changed
                if currentState !== lastState {
                    await MainActor.run {
                        self.applications = currentState.applications
                        self.filteredApplications = currentState.filteredApplications
                        self.isLoading = currentState.isLoading
                        self.error = currentState.error
                        self.selectedStatus = currentState.selectedStatus
                        self.searchQuery = currentState.searchQuery
                    }
                    lastState = currentState
                }
                
                // Poll every 100ms
                try? await SwiftConcurrency.sleep(nanoseconds: 100_000_000)
            }
        }
    }
    
    func setStatusFilter(_ status: ApplicationStatus?) {
        viewModel.setStatusFilter(status: status)
    }
    
    func setSearchQuery(_ query: String) {
        viewModel.setSearchQuery(query: query)
    }
    
    func addNewApplication(
        company: String,
        role: String,
        location: String? = nil,
        jobUrl: String? = nil,
        source: String? = nil,
        status: ApplicationStatus = ApplicationStatus.applied,
        appliedDateEpochMs: Int64? = nil,
        notes: String = ""
    ) {
        let dateMs = appliedDateEpochMs ?? Int64(Date().timeIntervalSince1970 * 1000)
        viewModel.addNewApplication(
            company: company,
            role: role,
            location: location,
            jobUrl: jobUrl,
            source: source,
            status: status,
            appliedDateEpochMs: dateMs,
            notes: notes
        )
    }
}

// MARK: - ApplicationDetailViewModel Bridge
class ApplicationDetailViewModel: ObservableObject {
    @Published var application: Application?
    @Published var statusHistory: [StatusHistory] = []
    @Published var tasks: [shared.Task] = []
    @Published var interviews: [Interview] = []
    @Published var contacts: [Contact] = []
    @Published var isLoading: Bool = false
    @Published var error: String?
    
    private let viewModel: shared.ApplicationDetailViewModel
    private var observationTask: ConcurrentTaskNever<Void>?
    
    init() {
        self.viewModel = KoinHelper.shared.getApplicationDetailViewModel()
        observeUiState()
    }
    
    deinit {
        observationTask?.cancel()
    }
    
    private func observeUiState() {
        // Use a simple polling mechanism to read StateFlow value
        observationTask = SwiftConcurrency.run { [weak self] in
            guard let self = self else { return }
            
            var lastState: ApplicationDetailUiState? = nil
            
            while !SwiftConcurrency.isCancelled {
                // Get current state using helper function
                let currentState = self.viewModel.getCurrentState()
                
                // Only update if state changed
                if currentState !== lastState {
                    await MainActor.run {
                        self.application = currentState.application
                        self.statusHistory = currentState.statusHistory
                        self.tasks = currentState.tasks
                        self.interviews = currentState.interviews
                        self.contacts = currentState.contacts
                        self.isLoading = currentState.isLoading
                        self.error = currentState.error
                    }
                    lastState = currentState
                }
                
                // Poll every 100ms
                try? await SwiftConcurrency.sleep(nanoseconds: 100_000_000)
            }
        }
    }
    
    func loadApplication(applicationId: String) {
        viewModel.loadApplication(id: applicationId)
    }
    
    func toggleTask(taskId: String) {
        viewModel.toggleTask(taskId: taskId)
    }
    
    func addTask(applicationId: String, title: String, dueDateEpochMs: Int64? = nil) {
        let kotlinLong = dueDateEpochMs != nil ? KotlinLong(value: dueDateEpochMs!) : nil
        viewModel.addTask(applicationId: applicationId, title: title, dueDateEpochMs: kotlinLong)
    }
    
    func updateApplication(application: Application) {
        viewModel.updateApplication(application: application)
    }
    
    func addContact(
        applicationId: String,
        contactName: String,
        contactRole: String? = nil,
        email: String? = nil,
        linkedInUrl: String? = nil,
        notes: String? = nil
    ) {
        viewModel.addContact(
            applicationId: applicationId,
            contactName: contactName,
            contactRole: contactRole,
            emailText: email,
            linkedInUrl: linkedInUrl,
            notesText: notes
        )
    }
    
    func deleteContact(contactId: String) {
        viewModel.deleteContact(contactId: contactId)
    }
}
