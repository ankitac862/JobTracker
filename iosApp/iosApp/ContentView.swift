import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = ApplicationsViewModel()
    @State private var showingAddSheet = false
    
    var body: some View {
        NavigationView {
            ApplicationsListView(viewModel: viewModel)
                .toolbar {
                    ToolbarItem(placement: .navigationBarLeading) {
                        Text("Job Tracker")
                            .font(.system(size: 20, weight: .bold))
                    }
                    ToolbarItem(placement: .navigationBarTrailing) {
                        Button(action: { showingAddSheet = true }) {
                            Image(systemName: "plus")
                                .font(.system(size: 18, weight: .semibold))
                                .foregroundColor(.white)
                                .frame(width: 36, height: 36)
                                .background(Color.blue)
                                .cornerRadius(18)
                        }
                    }
                }
        }
        .sheet(isPresented: $showingAddSheet) {
            AddEditApplicationView()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
