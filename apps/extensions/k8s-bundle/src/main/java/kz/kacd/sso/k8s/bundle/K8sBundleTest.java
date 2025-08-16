package kz.kacd.sso.k8s.bundle;

/**
 * Test class for k8s-bundle functionality
 */
public class K8sBundleTest {
    
    public static void main(String[] args) {
        try {
            System.out.println("Testing k8s-bundle...");
            
            // Test that we can load the facade
            K8sBundleFacade facade = new K8sBundleFacade(K8sBundleTest.class.getClassLoader());
            System.out.println("✓ K8sBundleFacade created successfully");
            
            // Test that we can get the client
            Object client = facade.getKubernetesClient();
            System.out.println("✓ KubernetesClient obtained: " + client.getClass().getName());
            
            // Test that we can close the facade
            facade.close();
            System.out.println("✓ K8sBundleFacade closed successfully");
            
            System.out.println("All tests passed!");
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}

