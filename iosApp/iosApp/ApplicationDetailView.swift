import SwiftUI
import shared

struct ApplicationDetailView: View {
    let applicationId: String
    @StateObject private var viewModel = ApplicationDetailViewModel()
    @State private var showAddTaskDialog = false
    @State private var newTaskTitle = ""
    @Environment(\.dismiss) private var dismiss
    
    private func statusColor(_ status: ApplicationStatus) -> Color {
        switch status {
        case .interview:
            return .blue
        case .applied:
            return .orange
        case .offer:
            return .green
        case .rejected:
            return .red
        default:
            return .blue
        }
    }
    
    var body: some View {
        ScrollView {
            if viewModel.isLoading {
                VStack {
                    Spacer()
                    ProgressView()
                        .scaleEffect(1.5)
                    Spacer()
                }
                .frame(minHeight: 400)
            } else if let application = viewModel.application {
                VStack(spacing: 24) {
                    // Hero Section
                    heroSection(application: application)
                    
                    // Overview Section
                    overviewSection(application: application)
                    
                    // Timeline Section
                    if !viewModel.statusHistory.isEmpty {
                        timelineSection(statusHistory: viewModel.statusHistory)
                    }
                    
                    // Tasks Section
                    tasksSection(tasks: viewModel.tasks)
                    
                    // Interviews Section
                    if !viewModel.interviews.isEmpty {
                        interviewsSection(interviews: viewModel.interviews)
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 16)
            } else {
                Text("Application not found")
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity)
                    .padding()
            }
        }
        .navigationTitle("Application Details")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                HStack(spacing: 8) {
                    Button(action: {}) {
                        Image(systemName: "pencil")
                    }
                    Button(action: {
                        // Delete action
                        dismiss()
                    }) {
                        Image(systemName: "trash")
                            .foregroundColor(.red)
                    }
                }
            }
        }
        .onAppear {
            viewModel.loadApplication(applicationId: applicationId)
        }
        .alert("Add Task", isPresented: $showAddTaskDialog) {
            TextField("Task Title", text: $newTaskTitle)
            Button("Cancel", role: .cancel) {
                newTaskTitle = ""
            }
            Button("Add") {
                if !newTaskTitle.isEmpty {
                    viewModel.addTask(applicationId: applicationId, title: newTaskTitle)
                    newTaskTitle = ""
                }
            }
        }
    }
    
    // MARK: - Hero Section
    @ViewBuilder
    private func heroSection(application: Application) -> some View {
        VStack(spacing: 0) {
            // Gradient background
            Rectangle()
                .fill(
                    LinearGradient(
                        gradient: Gradient(colors: [statusColor(application.status).opacity(0.3), Color.clear]),
                        startPoint: .top,
                        endPoint: .bottom
                    )
                )
                .frame(height: 100)
            
            // Company info overlay
            VStack(spacing: 12) {
                // Company Logo
                ZStack {
                    RoundedRectangle(cornerRadius: 12)
                        .fill(Color(.systemGray5))
                        .frame(width: 80, height: 80)
                    Text(String(application.company.prefix(1)).uppercased())
                        .font(.system(size: 32, weight: .bold))
                        .foregroundColor(.primary)
                }
                .offset(y: -40)
                .padding(.bottom, -40)
                
                Text(application.role)
                    .font(.system(size: 24, weight: .bold))
                    .multilineTextAlignment(.center)
                
                Text(application.company)
                    .font(.system(size: 16, weight: .medium))
                    .foregroundColor(.secondary)
                
                // Status and Location badges
                HStack(spacing: 8) {
                    // Status badge
                    HStack(spacing: 4) {
                        Image(systemName: "person.circle")
                            .font(.system(size: 12))
                        Text(application.status.name.capitalized)
                            .font(.system(size: 12, weight: .semibold))
                    }
                    .padding(.horizontal, 12)
                    .padding(.vertical, 6)
                    .background(statusColor(application.status).opacity(0.2))
                    .foregroundColor(statusColor(application.status))
                    .cornerRadius(20)
                    
                    // Location badge
                    if let location = application.location {
                        HStack(spacing: 4) {
                            Image(systemName: "location")
                                .font(.system(size: 12))
                            Text(location)
                                .font(.system(size: 12, weight: .medium))
                        }
                        .padding(.horizontal, 12)
                        .padding(.vertical, 6)
                        .background(Color(.systemGray5))
                        .foregroundColor(.secondary)
                        .cornerRadius(20)
                    }
                }
                
                // Open Job Link button
                Button(action: {}) {
                    HStack {
                        Image(systemName: "link")
                        Text("Open Job Link")
                            .fontWeight(.bold)
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 12)
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(8)
                }
                .padding(.horizontal, 20)
                .padding(.top, 12)
                
                // Applied info
                Text("Applied \(formatRelativeDate(epochMs: application.appliedDateEpochMs)) via \(application.source ?? "Unknown")")
                    .font(.system(size: 12))
                    .foregroundColor(.secondary)
                    .padding(.top, 8)
            }
            .padding(.bottom, 16)
        }
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
    
    // MARK: - Overview Section
    @ViewBuilder
    private func overviewSection(application: Application) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Overview")
                .font(.system(size: 18, weight: .bold))
                .padding(.leading, 4)
            
            VStack(alignment: .leading, spacing: 24) {
                HStack(spacing: 24) {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("SALARY")
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.secondary)
                        Text("Not specified")
                            .font(.system(size: 16, weight: .semibold))
                    }
                    
                    Spacer()
                    
                    VStack(alignment: .leading, spacing: 4) {
                        Text("WORK TYPE")
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.secondary)
                        Text("Not specified")
                            .font(.system(size: 16, weight: .semibold))
                    }
                }
                
                if !application.notes.isEmpty {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("NOTES")
                            .font(.system(size: 11, weight: .medium))
                            .foregroundColor(.secondary)
                        Text(application.notes)
                            .font(.system(size: 14))
                            .lineSpacing(4)
                    }
                }
                
                Divider()
                
                if let source = application.source {
                    HStack {
                        Text(source)
                            .font(.system(size: 12, weight: .medium))
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(Color(.systemGray5))
                            .cornerRadius(8)
                    }
                }
            }
            .padding(16)
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
        }
    }
    
    // MARK: - Timeline Section
    @ViewBuilder
    private func timelineSection(statusHistory: [StatusHistory]) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Timeline")
                .font(.system(size: 18, weight: .bold))
                .padding(.leading, 4)
            
            VStack(spacing: 0) {
                ForEach(Array(statusHistory.enumerated()), id: \.element.id) { index, history in
                    HStack(alignment: .top, spacing: 12) {
                        // Timeline dot and line
                        VStack(spacing: 0) {
                            Circle()
                                .fill(Color.blue)
                                .frame(width: 12, height: 12)
                            
                            if index < statusHistory.count - 1 {
                                Rectangle()
                                    .fill(Color(.systemGray4))
                                    .frame(width: 2, height: 40)
                            }
                        }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            if let fromStatus = history.fromStatus {
                                Text("\(fromStatus.name) → \(history.toStatus.name)")
                                    .font(.system(size: 14, weight: .bold))
                            } else {
                                Text(history.toStatus.name)
                                    .font(.system(size: 14, weight: .bold))
                            }
                            
                            Text("\(formatDate(epochMs: history.changedAtEpochMs)) • \(index == 0 ? "Completed" : "Pending")")
                                .font(.system(size: 12))
                                .foregroundColor(.secondary)
                            
                            if let note = history.note {
                                Text(note)
                                    .font(.system(size: 12))
                                    .foregroundColor(.secondary)
                                    .padding(.top, 2)
                            }
                        }
                        
                        Spacer()
                    }
                    .padding(.bottom, index < statusHistory.count - 1 ? 16 : 0)
                }
            }
            .padding(20)
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
        }
    }
    
    // MARK: - Tasks Section
    @ViewBuilder
    private func tasksSection(tasks: [shared.Task]) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("Tasks")
                    .font(.system(size: 18, weight: .bold))
                    .padding(.leading, 4)
                
                Spacer()
                
                Button(action: { showAddTaskDialog = true }) {
                    Text("ADD TASK")
                        .font(.system(size: 11, weight: .bold))
                        .foregroundColor(.blue)
                }
            }
            
            VStack(spacing: 8) {
                ForEach(tasks, id: \.id) { task in
                    TaskItemRow(
                        task: task,
                        onToggle: { viewModel.toggleTask(taskId: task.id) }
                    )
                }
                
                // Add task button
                Button(action: { showAddTaskDialog = true }) {
                    HStack(spacing: 12) {
                        Image(systemName: "plus")
                            .foregroundColor(.secondary)
                        Text("Add a new task...")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(.secondary)
                        Spacer()
                    }
                    .padding(12)
                    .overlay(
                        RoundedRectangle(cornerRadius: 12)
                            .stroke(Color(.systemGray4), style: StrokeStyle(lineWidth: 1, dash: [5]))
                    )
                }
            }
        }
    }
    
    // MARK: - Interviews Section
    @ViewBuilder
    private func interviewsSection(interviews: [Interview]) -> some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Interviews")
                .font(.system(size: 18, weight: .bold))
                .padding(.leading, 4)
            
            VStack(spacing: 8) {
                ForEach(interviews, id: \.interviewId) { interview in
                    InterviewItemRow(interview: interview)
                }
            }
        }
    }
    
    // MARK: - Helper Functions
    private func formatDate(epochMs: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(epochMs / 1000))
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM dd, yyyy"
        return formatter.string(from: date)
    }
    
    private func formatRelativeDate(epochMs: Int64) -> String {
        let now = Date().timeIntervalSince1970 * 1000
        let diff = now - Double(epochMs)
        let days = Int(diff / (1000 * 60 * 60 * 24))
        
        switch days {
        case 0:
            return "today"
        case 1:
            return "yesterday"
        case 2..<7:
            return "\(days) days ago"
        case 7..<30:
            return "\(days / 7) weeks ago"
        default:
            return formatDate(epochMs: epochMs)
        }
    }
}

// MARK: - Task Item Row
struct TaskItemRow: View {
    let task: shared.Task
    let onToggle: () -> Void
    
    var body: some View {
        Button(action: onToggle) {
            HStack(spacing: 12) {
                Image(systemName: task.isDone ? "checkmark.circle.fill" : "circle")
                    .font(.system(size: 22))
                    .foregroundColor(task.isDone ? .blue : .gray)
                
                Text(task.title)
                    .font(.system(size: 14, weight: .medium))
                    .strikethrough(task.isDone)
                    .foregroundColor(task.isDone ? .secondary : .primary)
                
                Spacer()
            }
            .padding(16)
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
        }
        .buttonStyle(PlainButtonStyle())
    }
}

// MARK: - Interview Item Row
struct InterviewItemRow: View {
    let interview: Interview
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(formatDate(epochMs: interview.scheduledDateEpochMs))
                .font(.system(size: 14, weight: .semibold))
            
            HStack {
                Text(interview.interviewMode.name)
                    .font(.system(size: 12))
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.blue.opacity(0.1))
                    .foregroundColor(.blue)
                    .cornerRadius(4)
                
                if let interviewer = interview.interviewerName {
                    Text(interviewer)
                        .font(.system(size: 12))
                        .foregroundColor(.secondary)
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
    
    private func formatDate(epochMs: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(epochMs / 1000))
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM dd, yyyy 'at' h:mm a"
        return formatter.string(from: date)
    }
}
