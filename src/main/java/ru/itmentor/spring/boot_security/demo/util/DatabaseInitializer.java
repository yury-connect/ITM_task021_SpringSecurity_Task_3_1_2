package ru.itmentor.spring.boot_security.demo.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.service.UserService;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserUtilService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Set;


/**
 * Класс CommandLineRunner в Spring Boot.
 * Этот компонент запускается после инициализации приложения
 * и позволяет выполнять необходимые проверки и действия по созданию и заполнению базы данных.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final UserUtilService userUtilService;
    private EntityManagerFactory entityManagerFactory;
    private UserService userService;
    private RoleService roleService;
    private UserGenerator userGenerator;



    @Autowired
    public DatabaseInitializer(EntityManagerFactory entityManagerFactory,
                               UserService userService,
                               RoleService roleService,
                               UserGenerator userGenerator,
                               UserUtilService userUtilService) {
        this.entityManagerFactory = entityManagerFactory;
        this.userService = userService;
        this.roleService = roleService;
        this.userGenerator = userGenerator;
        this.userUtilService = userUtilService;
    }


    /*
    Команда запускает Spring Boot приложение, собранное в JAR-файл (demo-0.0.1-SNAPSHOT.jar), который находится в директории target.
    // 'java -jar target/demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=test'
    'java -jar target/demo-0.0.1-SNAPSHOT.jar' — команда запускает Spring Boot приложение, собранное в JAR-файл (demo-0.0.1-SNAPSHOT.jar), который находится в директории target.
    '--spring.profiles.active=test' — эта опция активирует профиль test, указанный в настройках Spring. Активный профиль позволяет загружать специфичные для тестирования настройки, такие как application-test.yml или application-test.properties
     */


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!isDatabaseValid()) {
            resetDatabase();
            populateTestData();
        }
    }


    private boolean isDatabaseValid() {
        try {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            entityManager.getTransaction().begin();

            // Проверка наличия таблиц и их структуры
            List<User> allExistingUsers = entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
            List<Role> allExistingRoles = entityManager.createQuery("SELECT r FROM Role r", Role.class).getResultList();

            entityManager.getTransaction().commit();
            entityManager.close();

            // Проверка наличия обязательных пользователей и ролей
            if (!isRoleUniqueExist("ROLE_GUEST", allExistingRoles)) { throw new PersistenceException("The 'ROLE_GUEST' role does not exist or is not unique"); }
            if (!isRoleUniqueExist("ROLE_USER", allExistingRoles)) { throw new PersistenceException("The 'ROLE_USER' role does not exist or is not unique"); }
            if (!isRoleUniqueExist("ROLE_ADMIN", allExistingRoles)) { throw new PersistenceException("The 'ROLE_ADMIN' role does not exist or is not unique"); }
            if (!isRoleUniqueExist("ROLE_SUPERADMIN", allExistingRoles)) { throw new PersistenceException("The 'ROLE_SUPERADMIN' role does not exist or is not unique"); }

            if (!isUserUniqueExist("guest", "ROLE_GUEST", allExistingUsers)) { throw new PersistenceException("The user with userName = 'guest' does not exist or is not unique"); }
            if (!isUserUniqueExist("user", "ROLE_USER", allExistingUsers)) { throw new PersistenceException("The user with userName = 'user' does not exist or is not unique"); }
            if (!isUserUniqueExist("admin", "ROLE_ADMIN", allExistingUsers)) { throw new PersistenceException("The user with userName = 'admin' does not exist or is not unique"); }
            if (!isUserUniqueExist("supersadmin", "ROLE_SUPERADMIN", allExistingUsers)) { throw new PersistenceException("The user with userName = 'supersadmin' does not exist or is not unique"); }

            return true;
        } catch (PersistenceException e) {
            System.out.println("\n\n\tDatabase or tables are missing or invalid.:" + e.getMessage() + "\n\n\n");
            return false;
        }
    }

    // Возвращает true, если заданная роль присутствует в списке и встречается ровно 1 раз
    private boolean isRoleUniqueExist(String roleName, List<Role> roleList) {
        return roleList.stream()
                .filter(role -> roleName.equals(role.getName()))
                .count() == 1;
    }
    // Возвращает true, если пользователь с заданным 'userName' и с заданной 'roleName' присутствует в списке и встречается ровно 1 раз
    private boolean isUserUniqueExist(String userName, String roleName, List<User> userList) {
        return userList.stream()
                .filter(user -> userName.equals(user.getUserName()) &&
                        user.getRoles().stream().anyMatch(role -> roleName.equals(role.getName())))
                .count() == 1;
    }



    public void resetDatabase() {
        // Удаляем и пересоздаем базу данных (альтернатива: пересоздать схему через Hibernate)
//        EntityManager entityManager = entityManagerFactory.createEntityManager();
//        entityManager.getTransaction().begin();
//
//        // не  создаются корректно таблица roles так точно!
//        entityManager.createNativeQuery("DROP SCHEMA IF EXISTS public CASCADE").executeUpdate(); // удаляет схему 'public' и все объекты(таблицы), принадлежащие к ней. Использование CASCADE гарантирует, что удаление затронет все связанные зависимости.
//        entityManager.createNativeQuery("CREATE SCHEMA public").executeUpdate(); // создаёт пустую схему 'public' заново, чтобы база данных вернулась в состояние, готовое для создания новых таблиц и объектов.
//
//        entityManager.getTransaction().commit();
//        entityManager.close();  // Закрываем только EntityManager, но не EntityManagerFactory !!!

        // Очистка таблиц без удаления схемы
//        entityManager.createNativeQuery("TRUNCATE TABLE user_roles CASCADE").executeUpdate();
//        entityManager.createNativeQuery("TRUNCATE TABLE users CASCADE").executeUpdate();
//        entityManager.createNativeQuery("TRUNCATE TABLE roles CASCADE").executeUpdate();
//        System.out.println("\n\n\n\tDatabase reset completed.\n\n\n");
    }



    private void populateTestData() {
        // Создание необходимых ролей
        Role roleGuest = Role.builder().id(1).name("ROLE_GUEST").build();
        Role roleUser = Role.builder().id(2).name("ROLE_USER").build();
        Role roleAdmin = Role.builder().id(3).name("ROLE_ADMIN").build();
        Role roleSuperAdmin = Role.builder().id(4).name("ROLE_SUPERADMIN").build();

        roleService.createRole(roleGuest);
        roleService.createRole(roleUser);
        roleService.createRole(roleAdmin);
        roleService.createRole(roleSuperAdmin);

        // Создание тестовых пользователей
        List<User> allExistingUsers = userGenerator.generateUsers(4, roleService.findAllRoles());
        allExistingUsers.get(0).setUserName("guest");
        allExistingUsers.get(0).setRoles(Set.of(roleGuest));

        allExistingUsers.get(1).setUserName("user");
        allExistingUsers.get(1).setRoles(Set.of(roleGuest, roleUser));

        allExistingUsers.get(2).setUserName("admin");
        allExistingUsers.get(2).setRoles(Set.of(roleGuest, roleUser, roleAdmin));

        allExistingUsers.get(3).setUserName("superadmin");
        allExistingUsers.get(3).setRoles(Set.of(roleGuest, roleUser, roleAdmin, roleSuperAdmin));

        allExistingUsers.forEach(userService::createUser);

        // Создание рандомных тестовых пользователей
        userUtilService.generateTestData(6);

        System.out.println("Тестовые данные заполнены успешно // Test data populated successfully.");
    }
}
