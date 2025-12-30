import SwiftUI
import shared

struct AddEditApplicationView: View {
    var applicationId: String? = nil
    
    @State private var company = ""
    @State private var role = ""
    @State private var location = ""
    @State private var jobUrl = ""
    @State private var source = ""
    @State private var status: ApplicationStatus = .draft
    @State private var notes = ""
    @State private var appliedDate = Date()
    @State private var showStatusPicker = false
    
    @Environment(\.dismiss) private var dismiss
    @StateObject private var viewModel = ApplicationsViewModel()
    
    var isEditing: Bool {
        applicationId != nil
    }
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Form content
                ScrollView {
                    VStack(spacing: 20) {
                        // Core Details Section
                        VStack(spacing: 16) {
                            FormFieldView(
                                label: "Company",
                                placeholder: "e.g. Google",
                                text: $company
                            )
                            
                            FormFieldView(
                                label: "Role",
                                placeholder: "e.g. Senior Product Designer",
                                text: $role
                            )
                            
                            // Status Picker
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Status")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(.secondary)
                                    .padding(.leading, 4)
                                
                                Menu {
                                    ForEach(ApplicationStatus.allCases, id: \.self) { statusOption in
                                        Button(action: { status = statusOption }) {
                                            HStack {
                                                Text(statusOption.name.capitalized)
                                                if status == statusOption {
                                                    Image(systemName: "checkmark")
                                                }
                                            }
                                        }
                                    }
                                } label: {
                                    HStack {
                                        Text(status.name.capitalized)
                                            .foregroundColor(.primary)
                                        Spacer()
                                        Image(systemName: "chevron.down")
                                            .foregroundColor(.secondary)
                                    }
                                    .padding()
                                    .background(Color(.systemGray6))
                                    .cornerRadius(12)
                                }
                            }
                        }
                        
                        Divider()
                            .padding(.vertical, 4)
                        
                        // Logistics Section
                        VStack(spacing: 16) {
                            FormFieldWithIconView(
                                label: "Location",
                                placeholder: "e.g. San Francisco, CA",
                                icon: "location",
                                text: $location
                            )
                            
                            FormFieldWithIconView(
                                label: "Source",
                                placeholder: "e.g. LinkedIn, Referral",
                                icon: "person",
                                text: $source
                            )
                        }
                        
                        Divider()
                            .padding(.vertical, 4)
                        
                        // Details Section
                        VStack(spacing: 16) {
                            FormFieldWithIconView(
                                label: "Job Post URL",
                                placeholder: "https://...",
                                icon: "link",
                                text: $jobUrl
                            )
                            
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Notes")
                                    .font(.system(size: 14, weight: .medium))
                                    .foregroundColor(.secondary)
                                    .padding(.leading, 4)
                                
                                TextEditor(text: $notes)
                                    .frame(minHeight: 140)
                                    .padding(12)
                                    .background(Color(.systemGray6))
                                    .cornerRadius(12)
                                    .overlay(
                                        Group {
                                            if notes.isEmpty {
                                                Text("Add specific details, interview notes, or follow-up reminders...")
                                                    .foregroundColor(.secondary)
                                                    .padding(.horizontal, 16)
                                                    .padding(.vertical, 20)
                                            }
                                        },
                                        alignment: .topLeading
                                    )
                            }
                        }
                        
                        Spacer()
                            .frame(height: 100)
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 24)
                }
                
                // Bottom buttons
                VStack(spacing: 12) {
                    Button(action: saveApplication) {
                        Text("Save Application")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(Color.blue)
                            .cornerRadius(12)
                    }
                    .disabled(company.isEmpty || role.isEmpty)
                    .opacity(company.isEmpty || role.isEmpty ? 0.6 : 1)
                    
                    Button(action: { dismiss() }) {
                        Text("Cancel")
                            .font(.system(size: 16, weight: .semibold))
                            .foregroundColor(.secondary)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                    }
                }
                .padding(16)
                .background(Color(.systemBackground))
                .overlay(
                    Rectangle()
                        .fill(Color(.separator))
                        .frame(height: 0.5),
                    alignment: .top
                )
            }
            .navigationTitle(isEditing ? "Edit Application" : "Add Application")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button(action: { dismiss() }) {
                        Image(systemName: "xmark")
                            .foregroundColor(.secondary)
                    }
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Save") {
                        saveApplication()
                    }
                    .fontWeight(.semibold)
                    .foregroundColor(.blue)
                    .disabled(company.isEmpty || role.isEmpty)
                }
            }
            .onAppear {
                loadExistingApplication()
            }
        }
    }
    
    private func loadExistingApplication() {
        guard let id = applicationId else { return }
        if let app = viewModel.applications.first(where: { $0.id == id }) {
            company = app.company
            role = app.role
            location = app.location ?? ""
            jobUrl = app.jobUrl ?? ""
            source = app.source ?? ""
            status = app.status
            notes = app.notes
            appliedDate = Date(timeIntervalSince1970: TimeInterval(app.appliedDateEpochMs / 1000))
        }
    }
    
    private func saveApplication() {
        guard !company.isEmpty && !role.isEmpty else { return }
        
        if applicationId == nil {
            viewModel.addNewApplication(
                company: company,
                role: role,
                location: location.isEmpty ? nil : location,
                jobUrl: jobUrl.isEmpty ? nil : jobUrl,
                source: source.isEmpty ? nil : source,
                status: status,
                appliedDateEpochMs: Int64(appliedDate.timeIntervalSince1970 * 1000),
                notes: notes
            )
        } else {
            // Update existing - would need to implement update functionality
        }
        
        dismiss()
    }
}

// MARK: - Form Field View
struct FormFieldView: View {
    let label: String
    let placeholder: String
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(.system(size: 14, weight: .medium))
                .foregroundColor(.secondary)
                .padding(.leading, 4)
            
            TextField(placeholder, text: $text)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(12)
        }
    }
}

// MARK: - Form Field With Icon View
struct FormFieldWithIconView: View {
    let label: String
    let placeholder: String
    let icon: String
    @Binding var text: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(.system(size: 14, weight: .medium))
                .foregroundColor(.secondary)
                .padding(.leading, 4)
            
            HStack(spacing: 12) {
                Image(systemName: icon)
                    .foregroundColor(.secondary)
                    .frame(width: 20)
                
                TextField(placeholder, text: $text)
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(12)
        }
    }
}

// Note: ApplicationStatus.allCases extension is defined in ApplicationsListView.swift

#if DEBUG
struct AddEditApplicationView_Previews: PreviewProvider {
    static var previews: some View {
        AddEditApplicationView()
    }
}
#endif
