package pl.robloj.example.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import pl.robloj.example.app.dto.Employee;
import pl.robloj.example.app.dto.EmployeeRole;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String CONTENT_SECURITY_POLICY = "default-src 'self'; form-action 'self'; object-src 'none'; frame-ancestors 'none'; upgrade-insecure-requests; block-all-mixed-content; script-src 'self'";
    private static final String POLICY_DIRECTIVES = CONTENT_SECURITY_POLICY + "; report-uri /report-csp";
    private static final Long MAX_AGE_ONE_YEAR_IN_SECONDS = 31536000L;
    private static final String STRICT_TRANSPORT_SECURITY = "max-age=" + MAX_AGE_ONE_YEAR_IN_SECONDS + "; includeSubDomains; preload";

    private static final String[] OPENAPI_SPEC = {"/v3/**", "/swagger-ui/**", "/swagger-resources/**"};
    private static final String REST_PROFILE_PATH = "/profile/**";
    private static final String REST_EMPLOYEE_PATH = "/employees/**";
    private static final String REST_SALARIES_PATH = "/salaries/**";
    private static final String AUTHORITY_EMPLOYEE_READER = "EMPLOYEE_READER";
    private static final String AUTHORITY_EMPLOYEE_WRITER = "EMPLOYEE_WRITER";
    private static final String AUTHORITY_SALARY_READER = "EMPLOYEE_READER";
    private static final String AUTHORITY_SALARY_WRITER = "EMPLOYEE_WRTIER";

    @Autowired
    private ObjectMapper objectMapper;

    @Value("classpath:employees.json")
    private Resource employeesResourceFile;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() throws IOException {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<Employee> employees = objectMapper.readValue(
                employeesResourceFile.getFile(),
                typeFactory.constructCollectionType(List.class, Employee.class)
            );

        var rolesForEmployeeReader = List.of(
                EmployeeRole.CEO,
                EmployeeRole.DIRECTOR,
                EmployeeRole.MANAGER,
                EmployeeRole.RISK_OFFICER,
                EmployeeRole.COMPLIANCE,
                EmployeeRole.HR
            );
        var rolesForEmployeeWriter = List.of(
                EmployeeRole.HR
            );
        var rolesForSalaryReader = List.of(
                EmployeeRole.CEO,
                EmployeeRole.DIRECTOR,
                EmployeeRole.MANAGER,
                EmployeeRole.HR
            );
        var rolesForSalaryWriter = List.of(
                EmployeeRole.HR
            );

        var users = employees.stream().map(e -> {
            var role = e.getRole();
            var user = User.builder()
                .username(e.getFirstName().toLowerCase(Locale.ROOT) + "." + e.getLastName().toLowerCase(Locale.ROOT))
                .password(passwordEncoder().encode("password"));

            if (rolesForSalaryReader.contains(role)) {
                user.authorities(SecurityConfiguration.AUTHORITY_SALARY_READER);
            }
            if (rolesForSalaryWriter.contains(role)) {
                user.authorities(SecurityConfiguration.AUTHORITY_SALARY_WRITER);
            }
            if (rolesForEmployeeReader.contains(role)) {
                user.authorities(SecurityConfiguration.AUTHORITY_EMPLOYEE_READER);
            }
            if (rolesForEmployeeWriter.contains(role)) {
                user.authorities(SecurityConfiguration.AUTHORITY_EMPLOYEE_WRITER);
            }
            return user.build();
        }).toList();

        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers(OPENAPI_SPEC).permitAll()
                .requestMatchers(REST_PROFILE_PATH).permitAll()
                .requestMatchers(HttpMethod.GET, SecurityConfiguration.REST_EMPLOYEE_PATH)
                    .hasAuthority(SecurityConfiguration.AUTHORITY_EMPLOYEE_READER)
                .requestMatchers(HttpMethod.POST, SecurityConfiguration.REST_EMPLOYEE_PATH)
                    .hasAuthority(SecurityConfiguration.AUTHORITY_EMPLOYEE_WRITER)
                .requestMatchers(HttpMethod.PUT, SecurityConfiguration.REST_EMPLOYEE_PATH)
                    .hasAuthority(SecurityConfiguration.AUTHORITY_EMPLOYEE_WRITER)
                .requestMatchers(HttpMethod.GET, SecurityConfiguration.REST_SALARIES_PATH)
                    .hasAuthority(SecurityConfiguration.AUTHORITY_SALARY_READER)
                .requestMatchers(HttpMethod.POST, SecurityConfiguration.REST_SALARIES_PATH)
                    .hasAuthority(SecurityConfiguration.AUTHORITY_SALARY_WRITER)
                .anyRequest().denyAll()
            )
            .httpBasic(Customizer.withDefaults())
            .headers( h -> h
                .xssProtection((HeadersConfigurer.XXssConfig::disable))
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives((SecurityConfiguration.POLICY_DIRECTIVES))
                    .reportOnly()
                )
                .addHeaderWriter(new StaticHeadersWriter("Content-Security-Policy", SecurityConfiguration.CONTENT_SECURITY_POLICY))
                .addHeaderWriter(new StaticHeadersWriter("Strict-Transport-Security", SecurityConfiguration.STRICT_TRANSPORT_SECURITY))
            )
            .build();
    }
}
