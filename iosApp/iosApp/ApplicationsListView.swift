import SwiftUI
import shared

struct ApplicationsListView: View {
    @ObservedObject var viewModel: ApplicationsViewModel
    @State private var searchText: String = ""
    
    var body: some View {
        VStack(spacing: 0) {
            // Search Bar
            HStack {
                Image(systemName: "magnifyingglass")
                    .foregroundColor(.secondary)
                TextField("Search company or role...", text: $searchText)
                    .onChange(of: searchText) { oldValue, newValue in
                        viewModel.setSearchQuery(newValue)
                    }
            }
            .padding(12)
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .padding(.horizontal, 16)
            .padding(.top, 8)
            
            // Filter Chips
            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: 8) {
                    FilterChip(
                        label: "All",
                        isSelected: viewModel.selectedStatus == nil,
                        action: { viewModel.setStatusFilter(nil) }
                    )
                    
                    ForEach(ApplicationStatus.allCases, id: \.self) { status in
                        FilterChip(
                            label: status.name.capitalized,
                            isSelected: viewModel.selectedStatus == status,
                            action: { viewModel.setStatusFilter(status) }
                        )
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)
            }
            
            Divider()
            
            // Application List
            if viewModel.isLoading {
                Spacer()
                ProgressView()
                    .scaleEffect(1.2)
                Spacer()
            } else if viewModel.filteredApplications.isEmpty {
                Spacer()
                VStack(spacing: 12) {
                    Image(systemName: "briefcase")
                        .font(.system(size: 48))
                        .foregroundColor(.secondary)
                    Text("No applications yet")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    Text("Tap + to add your first application")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                Spacer()
            } else {
                ScrollView {
                    LazyVStack(spacing: 16) {
                        ForEach(viewModel.filteredApplications, id: \.id) { application in
                            NavigationLink(destination: ApplicationDetailView(applicationId: application.id)) {
                                ApplicationCard(application: application)
                            }
                            .buttonStyle(PlainButtonStyle())
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 16)
                }
            }
        }
        .navigationTitle("Job Tracker")
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .navigationBarTrailing) {
                NavigationLink(destination: SettingsView()) {
                    Image(systemName: "gearshape")
                }
            }
        }
    }
}

// MARK: - Filter Chip
struct FilterChip: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void
    
    var body: some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 14, weight: .medium))
                .padding(.horizontal, 16)
                .padding(.vertical, 8)
                .background(isSelected ? Color.blue : Color(.systemGray6))
                .foregroundColor(isSelected ? .white : .primary)
                .cornerRadius(20)
        }
    }
}

// MARK: - Application Card
struct ApplicationCard: View {
    let application: Application
    
    private var statusColor: Color {
        switch application.status {
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
        HStack(spacing: 0) {
            // Left border indicator
            Rectangle()
                .fill(statusColor)
                .frame(width: 4)
            
            VStack(alignment: .leading, spacing: 12) {
                HStack(alignment: .top) {
                    // Company Logo
                    ZStack {
                        RoundedRectangle(cornerRadius: 8)
                            .fill(Color(.systemGray5))
                            .frame(width: 40, height: 40)
                        Text(String(application.company.prefix(1)).uppercased())
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.primary)
                    }
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text(application.company)
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.primary)
                        Text(application.role)
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(.secondary)
                    }
                    
                    Spacer()
                    
                    // Status Badge
                    Text(application.status.name.capitalized)
                        .font(.system(size: 11, weight: .bold))
                        .padding(.horizontal, 10)
                        .padding(.vertical, 4)
                        .background(statusColor.opacity(0.2))
                        .foregroundColor(statusColor)
                        .cornerRadius(6)
                }
                
                // Location and Source
                HStack(spacing: 16) {
                    if let location = application.location {
                        HStack(spacing: 4) {
                            Image(systemName: "location")
                                .font(.system(size: 12))
                            Text(location)
                                .font(.system(size: 14))
                        }
                        .foregroundColor(.secondary)
                    }
                    
                    if let source = application.source {
                        HStack(spacing: 4) {
                            Image(systemName: source.lowercased().contains("linkedin") ? "person" : "globe")
                                .font(.system(size: 12))
                            Text(source)
                                .font(.system(size: 14))
                        }
                        .foregroundColor(.secondary)
                    }
                }
                .padding(.leading, 52)
                
                Divider()
                    .padding(.leading, 52)
                
                // Applied Date
                HStack {
                    Text("Applied: \(formatDate(epochMs: application.appliedDateEpochMs))")
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(.secondary)
                    
                    Spacer()
                    
                    if let source = application.source {
                        Text(source)
                            .font(.system(size: 10, weight: .medium))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 2)
                            .background(Color(.systemGray5))
                            .cornerRadius(4)
                            .foregroundColor(.secondary)
                    }
                }
                .padding(.leading, 52)
            }
            .padding(16)
        }
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: Color.black.opacity(0.05), radius: 4, x: 0, y: 2)
    }
    
    private func formatDate(epochMs: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(epochMs / 1000))
        let formatter = DateFormatter()
        formatter.dateFormat = "MMM dd"
        return formatter.string(from: date)
    }
}

// Extension to make ApplicationStatus iterable
extension ApplicationStatus: CaseIterable {
    public static var allCases: [ApplicationStatus] {
        return [.draft, .applied, .interview, .offer, .rejected]
    }
}
