package org.talend.ps.demo.customer.itests;

import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.scanFeatures;

import java.io.File;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.apache.karaf.tooling.exam.options.LogLevelOption;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.customerservice.Customer;
import com.example.customerservice.CustomerService;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class CustomerServiceTest {
    private static Logger LOG = LoggerFactory.getLogger(CustomerServiceTest.class);

    @Inject
    BundleContext bundleContext;
    
    @Inject
    CustomerService customerService;

    @Configuration
    public Option[] config() {
        MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf").version("2.3.0").type("tar.gz");
        MavenUrlReference cxfFeatures = maven().groupId("org.apache.cxf.karaf").artifactId("apache-cxf").type("xml").classifier("features").version("2.6.0");
        MavenArtifactUrlReference tesbFeatures = maven().groupId("org.talend.esb").artifactId("features").type("xml").version("5.1.2");
        MavenArtifactUrlReference customerFeatures = maven().groupId("org.talend.ps.demo").artifactId("customer.features").type("xml").version("0.1-SNAPSHOT");
        
        return new Option[]{
            karafDistributionConfiguration().frameworkUrl(karafUrl).karafVersion("2.3.0").name("Apache Karaf").unpackDirectory(new File("target/exam")),            
            logLevel(LogLevelOption.LogLevel.INFO),
            scanFeatures(cxfFeatures, "cxf"),
            scanFeatures(tesbFeatures, "tesb-locator-client").start(),
            scanFeatures(customerFeatures, "customer-provider-remote").start()
        };
    }
    
    @Test
    public void testCall() throws URISyntaxException, Exception {
        Thread.sleep(100000);
        Customer customer = customerService.getCustomerByName("testName");
        LOG.info("Got reply from customerService with customer: " + customer.getCustomerId());
    }

}
