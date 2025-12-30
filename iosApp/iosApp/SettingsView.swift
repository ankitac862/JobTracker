import SwiftUI
import shared

struct SettingsView: View {
    @StateObject private var viewModel = SettingsViewModel()
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var showPassword: Bool = false
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Authentication Section
                authenticationSection
                
                // Sync Section (only show when signed in)
                if viewModel.isSignedIn {
                    syncSection
                }
                
                // App Information Section
                aboutSection
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 20)
        }
        .background(Color(.systemGroupedBackground))
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.inline)
    }
    
    // MARK: - Authentication Section
    @ViewBuilder
    private var authenticationSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Authentication")
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(.secondary)
                .textCase(.uppercase)
                .padding(.leading, 4)
            
            VStack(spacing: 0) {
                if viewModel.isSignedIn {
                    // Signed In State
                    VStack(alignment: .leading, spacing: 16) {
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Signed in as")
                                    .font(.system(size: 12))
                                    .foregroundColor(.secondary)
                                Text(viewModel.currentUserId ?? "Unknown")
                                    .font(.system(size: 15, weight: .medium))
                            }
                            Spacer()
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.green)
                                .font(.system(size: 24))
                        }
                        .padding()
                        
                        Divider()
                        
                        Button(action: { viewModel.signOut() }) {
                            HStack {
                                Image(systemName: "rectangle.portrait.and.arrow.right")
                                    .font(.system(size: 16))
                                Text("Sign Out")
                                    .font(.system(size: 15, weight: .medium))
                                Spacer()
                            }
                            .foregroundColor(.red)
                            .padding()
                        }
                    }
                } else {
                    // Sign In/Up State
                    VStack(spacing: 16) {
                        VStack(spacing: 12) {
                            TextField("Email", text: $email)
                                .textContentType(.emailAddress)
                                .autocapitalization(.none)
                                .keyboardType(.emailAddress)
                                .padding()
                                .background(Color(.systemGray6))
                                .cornerRadius(10)
                            
                            HStack {
                                Group {
                                    if showPassword {
                                        TextField("Password", text: $password)
                                    } else {
                                        SecureField("Password", text: $password)
                                    }
                                }
                                .padding()
                                .background(Color(.systemGray6))
                                .cornerRadius(10)
                                
                                Button(action: { showPassword.toggle() }) {
                                    Image(systemName: showPassword ? "eye.slash" : "eye")
                                        .foregroundColor(.secondary)
                                        .frame(width: 44, height: 44)
                                }
                            }
                        }
                        
                        HStack(spacing: 12) {
                            Button(action: { viewModel.signIn(email: email, password: password) }) {
                                Group {
                                    if viewModel.isAuthLoading {
                                        ProgressView()
                                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    } else {
                                        Text("Sign In")
                                            .font(.system(size: 15, weight: .semibold))
                                    }
                                }
                                .frame(maxWidth: .infinity)
                                .padding(.vertical, 14)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                            }
                            .disabled(viewModel.isAuthLoading || email.isEmpty || password.isEmpty)
                            .opacity(email.isEmpty || password.isEmpty ? 0.6 : 1)
                            
                            Button(action: { viewModel.signUp(email: email, password: password) }) {
                                Text("Sign Up")
                                    .font(.system(size: 15, weight: .semibold))
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 14)
                                    .background(Color(.systemGray5))
                                    .foregroundColor(.primary)
                                    .cornerRadius(10)
                            }
                            .disabled(viewModel.isAuthLoading || email.isEmpty || password.isEmpty)
                            .opacity(email.isEmpty || password.isEmpty ? 0.6 : 1)
                        }
                        
                        if let authError = viewModel.authError {
                            Text(authError)
                                .font(.system(size: 13))
                                .foregroundColor(.red)
                                .multilineTextAlignment(.center)
                        }
                    }
                    .padding()
                }
            }
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.03), radius: 4, x: 0, y: 2)
        }
    }
    
    // MARK: - Sync Section
    @ViewBuilder
    private var syncSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Cloud Sync")
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(.secondary)
                .textCase(.uppercase)
                .padding(.leading, 4)
            
            VStack(spacing: 0) {
                // Last synced info
                HStack {
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Last synced")
                            .font(.system(size: 12))
                            .foregroundColor(.secondary)
                        if let lastSynced = viewModel.lastSyncedAtEpochMs {
                            Text(formatDate(epochMs: lastSynced))
                                .font(.system(size: 15, weight: .medium))
                        } else {
                            Text("Not synced yet")
                                .font(.system(size: 15, weight: .medium))
                                .foregroundColor(.secondary)
                        }
                    }
                    Spacer()
                    if viewModel.isSyncing {
                        ProgressView()
                    } else {
                        Image(systemName: "checkmark.icloud")
                            .font(.system(size: 24))
                            .foregroundColor(.green)
                    }
                }
                .padding()
                
                Divider()
                
                // Sync Now button
                Button(action: {
                    if let userId = viewModel.currentUserId {
                        viewModel.syncNow(userId: userId)
                    }
                }) {
                    HStack {
                        if viewModel.isSyncing {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            Text("Syncing...")
                                .padding(.leading, 8)
                        } else {
                            Image(systemName: "arrow.triangle.2.circlepath")
                            Text("Sync Now")
                                .padding(.leading, 8)
                        }
                    }
                    .font(.system(size: 15, weight: .semibold))
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 14)
                    .background(Color.green)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                }
                .disabled(viewModel.isSyncing)
                .padding()
                
                if let syncError = viewModel.syncError {
                    Text("Error: \(syncError)")
                        .font(.system(size: 13))
                        .foregroundColor(.red)
                        .padding(.horizontal)
                        .padding(.bottom)
                }
            }
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.03), radius: 4, x: 0, y: 2)
        }
    }
    
    // MARK: - About Section
    @ViewBuilder
    private var aboutSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("About")
                .font(.system(size: 13, weight: .semibold))
                .foregroundColor(.secondary)
                .textCase(.uppercase)
                .padding(.leading, 4)
            
            VStack(spacing: 0) {
                HStack {
                    Text("Version")
                        .font(.system(size: 15))
                    Spacer()
                    Text("1.0.0")
                        .font(.system(size: 15))
                        .foregroundColor(.secondary)
                }
                .padding()
                
                Divider()
                    .padding(.leading, 16)
                
                HStack {
                    Text("Build")
                        .font(.system(size: 15))
                    Spacer()
                    Text("1")
                        .font(.system(size: 15))
                        .foregroundColor(.secondary)
                }
                .padding()
            }
            .background(Color(.systemBackground))
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(0.03), radius: 4, x: 0, y: 2)
        }
    }
    
    private func formatDate(epochMs: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(epochMs / 1000))
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

#if DEBUG
struct SettingsView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            SettingsView()
        }
    }
}
#endif

