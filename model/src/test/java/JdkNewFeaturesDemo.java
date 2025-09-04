import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JDK 9-21 新特性演示类
 * 展示各版本的新特性与JDK 8写法的对比
 */
public class JdkNewFeaturesDemo {

  // ===== JDK 9 新特性 =====

  /**
   * JDK 9: 集合工厂方法 vs JDK 8
   * JDK 8需要通过Arrays.asList()或者手动添加元素
   * JDK 9提供了简洁的工厂方法创建不可变集合
   * List.of()、Set.of()、Map.of() 系列方法提供了简洁的语法来创建内容确定的、不可变的集合。这在很多场景下非常有用，例如配置项、常量列表等，可以有效避免意外修改导致的bug
   */
  public void collectionFactoryMethods() {
    System.out.println("=== JDK 9: 集合工厂方法 ===");

    // JDK 8 写法 - 创建不可变List
    List<String> jdk8List = Arrays.asList("Apple", "Banana", "Cherry");
    List<String> jdk8ImmutableList = Collections.unmodifiableList(
        new ArrayList<>(Arrays.asList("Apple", "Banana", "Cherry"))
    );

    // JDK 9 写法 - 直接创建不可变List
    List<String> jdk9List = List.of("Apple", "Banana", "Cherry");

    // JDK 8 写法 - 创建不可变Set
    Set<String> jdk8Set = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList("Apple", "Banana", "Cherry"))
    );

    // JDK 9 写法 - 直接创建不可变Set
    Set<String> jdk9Set = Set.of("Apple", "Banana", "Cherry");

    // JDK 8 写法 - 创建不可变Map
    Map<String, Integer> jdk8Map = Collections.unmodifiableMap(
        new HashMap<String, Integer>() {{
          put("Apple", 1);
          put("Banana", 2);
          put("Cherry", 3);
        }}
    );

    // JDK 9 写法 - 直接创建不可变Map
    Map<String, Integer> jdk9Map = Map.of(
        "Apple", 1,
        "Banana", 2,
        "Cherry", 3
    );

    System.out.println("JDK 9 List: " + jdk9List);
    System.out.println("JDK 9 Set: " + jdk9Set);
    System.out.println("JDK 9 Map: " + jdk9Map);
  }

  /**
   * JDK 9: Stream API 增强
   * 新增了takeWhile, dropWhile, iterate, ofNullable方法
   * takeWhile 从流的开始处按条件获取元素，一旦条件不满足就停止（类似 break）
   * dropWhile 从流的开始处按条件丢弃元素，一旦条件不满足就开始保留剩余所有元素
   */
  public void streamApiEnhancements() {
    System.out.println("\n=== JDK 9: Stream API 增强 ===");

    List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    // JDK 8 写法 - 获取前几个满足条件的元素（复杂）
    List<Integer> jdk8TakeWhile = numbers.stream()
        .filter(n -> n < 5)
        .collect(Collectors.toList());

    // JDK 9 写法 - takeWhile：获取满足条件的前缀元素
    List<Integer> jdk9TakeWhile = numbers.stream()
        .takeWhile(n -> n < 5)
        .collect(Collectors.toList());

    // JDK 9 写法 - dropWhile：跳过满足条件的前缀元素
    List<Integer> jdk9DropWhile = numbers.stream()
        .dropWhile(n -> n < 5)
        .collect(Collectors.toList());

    // JDK 8 写法 - 处理可能为null的值
    String nullableValue = null;
    List<String> jdk8OfNullable = Arrays.stream(new String[]{nullableValue})
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    // JDK 9 写法 - ofNullable：优雅处理可能为null的值
    List<String> jdk9OfNullable = Stream.ofNullable(nullableValue)
        .collect(Collectors.toList());

    System.out.println("takeWhile结果: " + jdk9TakeWhile); // [1, 2, 3, 4]
    System.out.println("dropWhile结果: " + jdk9DropWhile); // [5, 6, 7, 8, 9, 10]
    System.out.println("ofNullable结果: " + jdk9OfNullable); // []
  }

  /**
   * JDK 9: Optional API 增强
   * 新增了ifPresentOrElse, stream, or方法
   */
  public void optionalEnhancements() {
    System.out.println("\n=== JDK 9: Optional API 增强 ===");

    Optional<String> presentValue = Optional.of("Hello");
    Optional<String> emptyValue = Optional.empty();

    // JDK 8 写法 - 存在时执行操作，否则执行其他操作
    if (presentValue.isPresent()) {
      System.out.println("JDK 8 - 值存在: " + presentValue.get());
    } else {
      System.out.println("JDK 8 - 值不存在，执行默认操作");
    }

    // JDK 9 写法 - ifPresentOrElse
    presentValue.ifPresentOrElse(
        value -> System.out.println("JDK 9 - 值存在: " + value),
        () -> System.out.println("JDK 9 - 值不存在，执行默认操作")
    );

    // JDK 9 写法 - stream：将Optional转换为Stream
    List<String> streamResult = presentValue.stream()
        .collect(Collectors.toList());
    System.out.println("Optional转Stream结果: " + streamResult);

    // JDK 9 写法 - or：提供备选Optional
    Optional<String> result = emptyValue.or(() -> Optional.of("默认值"));
    System.out.println("or方法结果: " + result.get());
  }

  // ===== JDK 10 新特性 =====

  /**
   * JDK 10: var 关键字（局部变量类型推断）
   * 简化局部变量声明，编译器自动推断类型
   */
  public void localVariableTypeInference() {
    System.out.println("\n=== JDK 10: var 关键字 ===");

    // JDK 8 写法 - 显式类型声明
    String jdk8String = "Hello World";
    List<String> jdk8List = new ArrayList<>();
    Map<String, Integer> jdk8Map = new HashMap<>();

    // JDK 10 写法 - var类型推断
    var jdk10String = "Hello World";  // 推断为String
    var jdk10List = new ArrayList<String>(); // 推断为ArrayList<String>
    var jdk10Map = Map.of("key", 1); // 推断为Map<String, Integer>

    // 注意：var只能用于局部变量，不能用于字段、方法参数、返回类型
    var numbers = List.of(1, 2, 3, 4, 5);
    for (var number : numbers) { // 在增强for循环中使用var
      System.out.print(number + " ");
    }
    System.out.println();

    System.out.println("var推断类型演示完成");
  }

  // ===== JDK 11 新特性 =====

  /**
   * JDK 11: String API 增强
   * 新增了isBlank, lines, strip, repeat等方法
   */
  public void stringApiEnhancements() {
    System.out.println("\n=== JDK 11: String API 增强 ===");

    String testString = "  Hello World  \n  Java 11  \n";
    String blankString = "   ";

    // JDK 8 写法 - 检查字符串是否为空或只包含空白字符
    boolean jdk8IsBlank = blankString.trim().isEmpty();

    // JDK 11 写法 - isBlank方法
    boolean jdk11IsBlank = blankString.isBlank();
    System.out.println("字符串是否为空白: " + jdk11IsBlank);

    // JDK 8 写法 - 去除首尾空白字符（只能处理ASCII空白字符）
    String jdk8Strip = testString.trim();

    // JDK 11 写法 - strip方法（支持Unicode空白字符）
    String jdk11Strip = testString.strip();
    System.out.println("strip结果: [" + jdk11Strip + "]");

    // JDK 11 写法 - 只去除开头或结尾空白
    System.out.println("stripLeading: [" + testString.stripLeading() + "]");
    System.out.println("stripTrailing: [" + testString.stripTrailing() + "]");

    // JDK 8 写法 - 按行分割字符串
    String[] jdk8Lines = testString.split("\\R");

    // JDK 11 写法 - lines方法返回Stream<String>
    List<String> jdk11Lines = testString.lines()
        .collect(Collectors.toList());
    System.out.println("按行分割: " + jdk11Lines);

    // JDK 11 写法 - repeat方法重复字符串
    String repeated = "Ha".repeat(3);
    System.out.println("重复字符串: " + repeated); // HaHaHa
  }

  /**
   * JDK 11: HTTP Client API
   * JDK 9引入孵化，JDK 11正式发布，替代旧的HttpURLConnection
   */
  public void httpClientDemo() {
    System.out.println("\n=== JDK 11: HTTP Client API ===");

    try {
      // JDK 11 写法 - 新的HTTP Client
      HttpClient client = HttpClient.newBuilder()
          .connectTimeout(Duration.ofSeconds(10))
          .build();

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("https://api.github.com"))
          .timeout(Duration.ofSeconds(5))
          .GET()
          .build();

      HttpResponse<String> response = client.send(request,
          HttpResponse.BodyHandlers.ofString());

      System.out.println("HTTP响应状态: " + response.statusCode());
      System.out.println("响应头Content-Type: " +
          response.headers().firstValue("content-type").orElse("未知"));

    } catch (IOException | InterruptedException e) {
      System.out.println("HTTP请求失败: " + e.getMessage());
    }

    // 注意：JDK 8需要使用HttpURLConnection或第三方库如Apache HttpClient、OkHttp
    System.out.println("新HTTP Client更加现代化和易用");
  }

  // ===== JDK 12-13 新特性 =====

  /**
   * JDK 12-13: Switch 表达式（预览特性，JDK 14正式）
   * 支持表达式形式的switch，可以返回值
   */
  public void switchExpressions() {
    System.out.println("\n=== JDK 12-13: Switch 表达式 ===");

    String day = "MONDAY";

    // JDK 8 写法 - 传统switch语句
    String jdk8Result;
    switch (day) {
      case "MONDAY":
      case "TUESDAY":
      case "WEDNESDAY":
      case "THURSDAY":
      case "FRIDAY":
        jdk8Result = "工作日";
        break;
      case "SATURDAY":
      case "SUNDAY":
        jdk8Result = "周末";
        break;
      default:
        jdk8Result = "未知";
        break;
    }

    // JDK 12+ 写法 - switch表达式（箭头语法）
    String jdk12Result = switch (day) {
      case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> "工作日";
      case "SATURDAY", "SUNDAY" -> "周末";
      default -> "未知";
    };

    // JDK 13+ 写法 - yield关键字（用于复杂逻辑）
    String jdk13Result = switch (day) {
      case "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" -> {
        System.out.println("这是一个工作日");
        yield "工作日";
      }
      case "SATURDAY", "SUNDAY" -> {
        System.out.println("这是周末");
        yield "周末";
      }
      default -> "未知";
    };

    System.out.println("JDK 8 结果: " + jdk8Result);
    System.out.println("JDK 12+ 结果: " + jdk12Result);
    System.out.println("JDK 13+ 结果: " + jdk13Result);
  }

  // ===== JDK 14 新特性 =====

  /**
   * JDK 14: 有用的NullPointerException信息
   * 增强的NPE异常信息，能准确定位哪个引用为null
   * 需要JVM参数：-XX:+ShowCodeDetailsInExceptionMessages 默认开启的
   */
  public void helpfulNullPointerExceptions() {
    System.out.println("\n=== JDK 14: 增强的NullPointerException ===");

    try {
      String str = null;
      // JDK 14之前：只显示行号，不知道具体哪个引用为null
      // JDK 14+：显示详细信息，例如"Cannot invoke String.length() because 'str' is null"
      int length = str.length();
    } catch (NullPointerException e) {
      System.out.println("捕获到NPE: " + e.getMessage());
    }
  }

  /**
   * JDK 14: Records（预览特性，JDK 16正式）
   * 简化数据类的创建，自动生成构造器、getter、equals、hashCode、toString
   */
  public void recordsDemo() {
    System.out.println("\n=== JDK 14: Records（数据类） ===");

    // JDK 14+ 写法 - 使用Record
    // record Person(String name, int age) {} // 在实际使用中定义

    // 模拟Record的效果（实际Record更简洁）
    var person = new PersonRecord("张三", 25);
    var person2 = new PersonRecord("张三", 25);

    System.out.println("Person: " + person);
    System.out.println("姓名: " + person.name());
    System.out.println("年龄: " + person.age());
    System.out.println("两个对象相等: " + person.equals(person2));
    System.out.println("HashCode: " + person.hashCode());
  }

  // 模拟Record类（实际使用时更简单）
  record PersonRecord(String name, int age) {
    // Record自动生成构造器、getter、equals、hashCode、toString
    // 可以添加自定义方法和验证逻辑
    public PersonRecord {
      if (age < 0) {
        throw new IllegalArgumentException("年龄不能为负数");
      }
    }

    public boolean isAdult() {
      return age >= 18;
    }
  }

  // ===== JDK 15 新特性 =====

  /**
   * JDK 15: Text Blocks（文本块）正式版
   * JDK 13-14为预览特性，JDK 15正式发布
   */
  public void textBlocks() {
    System.out.println("\n=== JDK 15: Text Blocks（文本块） ===");

    // JDK 8 写法 - 多行字符串需要转义和拼接
    String jdk8Json = "{\n" +
        "  \"name\": \"张三\",\n" +
        "  \"age\": 25,\n" +
        "  \"city\": \"北京\"\n" +
        "}";

    String jdk8Sql = "SELECT id, name, email\n" +
        "FROM users\n" +
        "WHERE age > 18\n" +
        "ORDER BY name";

    // JDK 15 写法 - 文本块，保持格式，无需转义
    String jdk15Json = """
        {
          "name": "张三",
          "age": 25,
          "city": "北京"
        }
        """;

    String jdk15Sql = """
        SELECT id, name, email
        FROM users
        WHERE age > 18
        ORDER BY name
        """;

    // 文本块支持字符串插值（通过格式化方法）
    String name = "李四";
    int age = 30;
    String formattedJson = """
        {
          "name": "%s",
          "age": %d,
          "city": "上海"
        }
        """.formatted(name, age);

    System.out.println("JDK 15 JSON:\n" + jdk15Json);
    System.out.println("JDK 15 SQL:\n" + jdk15Sql);
    System.out.println("格式化文本块:\n" + formattedJson);
  }

  // ===== JDK 16 新特性 =====

  /**
   * JDK 16: Pattern Matching for instanceof
   * 简化instanceof的使用，自动进行类型转换
   */
  public void patternMatchingInstanceof() {
    System.out.println("\n=== JDK 16: Pattern Matching for instanceof ===");

    Object obj = "Hello World";

    // JDK 8 写法 - 需要显式类型转换
    if (obj instanceof String) {
      String str = (String) obj;
      System.out.println("JDK 8 - 字符串长度: " + str.length());
    }

    // JDK 16 写法 - 模式匹配，自动类型转换
    if (obj instanceof String str) {
      System.out.println("JDK 16 - 字符串长度: " + str.length());
    }

    // 更复杂的例子
    Object[] objects = {"Hello", 42, 3.14, List.of(1, 2, 3)};

    for (Object o : objects) {
      // JDK 16 写法 - 链式模式匹配
      switch (o) {
        case String s -> System.out.println("字符串: " + s.toUpperCase());
        case Integer i -> System.out.println("整数: " + (i * 2));
        case Double d -> System.out.println("小数: " + String.format("%.2f", d));
        case List<?> list -> System.out.println("列表大小: " + list.size());
        default -> System.out.println("其他类型: " + o.getClass().getSimpleName());
      }
    }
  }

  /**
   * JDK 8 ~ 15 写法：使用 collect(Collectors.toList()) 收集为 List，返回的 List 一般是 ArrayList，可以修改
   * JDK 16+ 写法：使用 stream.toList() 收集为 List，返回的是不可变 List (unmodifiable)，不能修改
   */
  public void toList() {
    System.out.println("\n=== JDK 16: toList ===");

    List<String> jdk8_15_list = Stream.of("A", "B", "C").collect(Collectors.toList());
    System.out.println("JDK 8 - 15: " + jdk8_15_list);
    // 可以正常添加元素
    jdk8_15_list.add("D");
    System.out.println("JDK 8 - 15 After add: " + jdk8_15_list);

    List<String> jdk_16_list = Stream.of("A", "B", "C").toList();
    System.out.println("JDK 16+: " + jdk_16_list);
    try {
      jdk_16_list.add("D"); // 会抛 UnsupportedOperationException
    } catch (UnsupportedOperationException e) {
      System.out.println("JDK 16+: Cannot modify list from stream.toList()");
    }
  }

  // ===== JDK 17 新特性（LTS版本） =====

  /**
   * JDK 17: Sealed Classes（密封类）
   * 限制哪些类可以继承或实现，提供更好的类型安全
   */
  public void sealedClassesDemo() {
    System.out.println("\n=== JDK 17: Sealed Classes（密封类） ===");

    Shape circle = new Circle(5.0);
    Shape rectangle = new Rectangle(4.0, 6.0);

    System.out.println("圆形面积: " + calculateArea(circle));
    System.out.println("矩形面积: " + calculateArea(rectangle));
  }

  // 密封类定义 - 只允许指定的类继承
  public abstract sealed class Shape
      permits Circle, Rectangle, Triangle {

    public abstract double area();
  }

  public final class Circle extends Shape {
    private final double radius;

    public Circle(double radius) {
      this.radius = radius;
    }

    @Override
    public double area() {
      return Math.PI * radius * radius;
    }

    public double radius() {
      return radius;
    }
  }

  public final class Rectangle extends Shape {
    private final double width, height;

    public Rectangle(double width, double height) {
      this.width = width;
      this.height = height;
    }

    @Override
    public double area() {
      return width * height;
    }

    public double width() {
      return width;
    }

    public double height() {
      return height;
    }
  }

  public non-sealed class Triangle extends Shape {
    // non-sealed允许进一步继承
    private final double base, height;

    public Triangle(double base, double height) {
      this.base = base;
      this.height = height;
    }

    @Override
    public double area() {
      return 0.5 * base * height;
    }
  }

  // 使用密封类的模式匹配（结合JDK 17+特性）
  public double calculateArea(Shape shape) {
    return switch (shape) {
      case Circle c -> Math.PI * c.radius() * c.radius();
      case Rectangle r -> r.width() * r.height();
      case Triangle t -> t.area();
      // 由于是密封类，编译器知道所有可能的子类型，无需default
    };
  }

  // ===== JDK 18-19 新特性 =====

  /**
   * JDK 18: UTF-8作为默认字符集
   * JDK 19: Virtual Threads（虚拟线程）
   */
  public void jdk18_19Features() {
    System.out.println("\n=== JDK 18-19: 其他新特性 ===");

    // JDK 18: UTF-8现在是默认字符集
    System.out.println("默认字符集: " + System.getProperty("file.encoding"));

    // 传统线程创建方式
    Thread traditionalThread = new Thread(() -> System.out.println("传统线程: " + Thread.currentThread().getName()));
    traditionalThread.start();

    try {
      traditionalThread.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // JDK 19: Virtual Threads演示（简化版本）
    System.out.println("虚拟线程可以创建数百万个轻量级线程");
  }

  // ===== JDK 20-21 新特性 =====

  /**
   * JDK 20-21: Virtual Threads正式版, Pattern Matching增强等
   * 虚拟线程是JVM层面的轻量级线程，由JVM调度而非操作系统，可以创建数百万个。它们通过“挂载”到少量平台线程上实现并发，阻塞时会自动卸载，极大地提高了I/O密集型应用的吞吐量。
   */
  public void jdk20_21Features() {
    System.out.println("\n=== JDK 20-21: 最新特性 ===");

    // JDK 21: Virtual Threads正式版
    // 创建虚拟线程的几种方式

    // 方式1: Thread.ofVirtual()
    Thread virtualThread1 = Thread.ofVirtual()
        .name("virtual-thread-1")
        .start(() -> System.out.println("虚拟线程1运行在: " + Thread.currentThread()));
    try {
      virtualThread1.join();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // 方式2: Executors.newVirtualThreadPerTaskExecutor() 自动关闭executor
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < 5; i++) {
        final int taskId = i;
        executor.submit(() -> System.out.println("任务 " + taskId + " 运行在虚拟线程: " +
            Thread.currentThread().isVirtual()));
      }
    }
  }

  /**
   * JDK 21: 集合的序列化增强和其他改进
   */
  public void collectionSequenceEnhancements() {
    System.out.println("\n=== JDK 21: 集合序列化增强 ===");

    // JDK 21: SequencedCollection接口
    // List现在实现了SequencedCollection，提供了新方法

    var list = new ArrayList<>(List.of("first", "second", "third", "last"));

    // 新的序列化集合方法
    System.out.println("第一个元素: " + list.getFirst()); // 等同于get(0)
    System.out.println("最后一个元素: " + list.getLast());  // 等同于get(size()-1)

    // 添加到开头和结尾
    list.addFirst("new-first");
    list.addLast("new-last");

    System.out.println("修改后的列表: " + list);

    // 移除第一个和最后一个
    String removedFirst = list.removeFirst();
    String removedLast = list.removeLast();

    System.out.println("移除的第一个: " + removedFirst);
    System.out.println("移除的最后一个: " + removedLast);
    System.out.println("最终列表: " + list);

    // 反转视图
    var reversedView = list.reversed();
    System.out.println("反转视图: " + reversedView);
  }

  /**
   * 综合演示：使用多个JDK新特性的复合示例
   */
  public void comprehensiveExample() {
    System.out.println("\n=== 综合演示：多版本特性结合使用 ===");

    // 使用JDK 9的集合工厂方法
    var users = List.of(
        new PersonRecord("张三", 25),
        new PersonRecord("李四", 17),
        new PersonRecord("王五", 30),
        new PersonRecord("赵六", 22)
    );

    // 使用JDK 11的字符串方法和JDK 16的模式匹配
    users.stream()
        .filter(PersonRecord::isAdult) // JDK 14 Records
        .map(person -> {
          // JDK 15 文本块和字符串方法
          return """
              用户信息：
              姓名：%s
              年龄：%d岁
              """.formatted(person.name(), person.age()).strip();
        })
        .forEach(System.out::println);

    // JDK 12+ Switch表达式
    users.forEach(person -> {
      String category = switch (person.age() / 10) {
        case 0, 1 -> "少年";
        case 2 -> "青年";
        case 3, 4 -> "中年";
        default -> "其他";
      };
      System.out.println(person.name() + " 属于：" + category);
    });
  }

  /**
   * 性能对比演示：展示新特性对性能的影响
   */
  public void performanceComparison() {
    System.out.println("\n=== 性能对比演示 ===");

    // 集合创建性能对比
    long startTime = System.nanoTime();

    // JDK 8 方式创建大量不可变集合
    for (int i = 0; i < 10000; i++) {
      List<String> jdk8List = Collections.unmodifiableList(
          Arrays.asList("a", "b", "c", "d", "e")
      );
    }
    long jdk8Time = System.nanoTime() - startTime;

    startTime = System.nanoTime();

    // JDK 9+ 方式创建大量不可变集合
    for (int i = 0; i < 10000; i++) {
      List<String> jdk9List = List.of("a", "b", "c", "d", "e");
    }
    long jdk9Time = System.nanoTime() - startTime;

    System.out.printf("JDK 8 集合创建耗时: %d ns%n", jdk8Time);
    System.out.printf("JDK 9+ 集合创建耗时: %d ns%n", jdk9Time);
    System.out.printf("性能提升: %.2f%%%n",
        ((double) (jdk8Time - jdk9Time) / jdk8Time) * 100);

    // 字符串处理性能对比
    String testText = "  Hello\n  World\n  Java\n  ";

    startTime = System.nanoTime();
    for (int i = 0; i < 100000; i++) {
      // JDK 8 方式
      String[] lines = testText.trim().split("\\n");
      List<String> result = Arrays.stream(lines)
          .map(String::trim)
          .collect(Collectors.toList());
    }
    long jdk8StringTime = System.nanoTime() - startTime;

    startTime = System.nanoTime();
    for (int i = 0; i < 100000; i++) {
      // JDK 11 方式
      List<String> result = testText.lines()
          .map(String::strip)
          .collect(Collectors.toList());
    }
    long jdk11StringTime = System.nanoTime() - startTime;

    System.out.printf("JDK 8 字符串处理耗时: %d ns%n", jdk8StringTime);
    System.out.printf("JDK 11 字符串处理耗时: %d ns%n", jdk11StringTime);
  }

  /**
   * 实用工具方法：展示新特性在实际开发中的应用
   */
  public void practicalUtilities() {
    System.out.println("\n=== 实用工具方法演示 ===");

    // 使用JDK新特性构建的实用方法

    // 1. 安全的字符串处理工具（使用JDK 11特性）
    List<String> inputs = List.of("  hello  ", "", "   ", "world");
    List<String> cleanedInputs = safeStringProcess(inputs);
    System.out.println("清理后的字符串: " + cleanedInputs);

    // 2. 类型安全的对象处理（使用JDK 16模式匹配）
    List<Object> mixedObjects = List.of("text", 42, 3.14, true, List.of(1, 2, 3));
    processObjects(mixedObjects);

    // 3. 配置文件处理（使用JDK 15文本块）
    String config = generateConfig("MyApp", "localhost", 8080, true);
    System.out.println("生成的配置:\n" + config);
  }

  /**
   * 安全的字符串处理工具方法
   * 利用JDK 9的Stream.ofNullable和JDK 11的字符串方法
   */
  private List<String> safeStringProcess(List<String> inputs) {
    return inputs.stream()
        .flatMap(Stream::ofNullable) // JDK 9: 处理null值
        .filter(s -> !s.isBlank())   // JDK 11: 检查是否为空白
        .map(String::strip)          // JDK 11: 去除首尾空白
        .collect(Collectors.toList());
  }

  /**
   * 类型安全的对象处理
   * 利用JDK 16的模式匹配和JDK 12+的Switch表达式
   */
  private void processObjects(List<Object> objects) {
    objects.forEach(obj -> {
      String result = switch (obj) {
        case String s -> "字符串: " + s.toUpperCase();
        case Integer i -> "整数: " + (i > 0 ? "正数" : i < 0 ? "负数" : "零");
        case Double d -> "浮点数: " + String.format("%.2f", d);
        case Boolean b -> "布尔值: " + (b ? "真" : "假");
        case List<?> list -> "列表，包含" + list.size() + "个元素";
        case null -> "空值";
        default -> "未知类型: " + obj.getClass().getSimpleName();
      };
      System.out.println(result);
    });
  }

  /**
   * 配置文件生成器
   * 利用JDK 15文本块和字符串格式化
   */
  private String generateConfig(String appName, String host, int port, boolean debug) {
    return """
        # %s 应用配置文件
        # 生成时间: %s
        
        [server]
        host=%s
        port=%d
        debug=%s
        
        [database]
        url=jdbc:mysql://%s:%d/app_db
        driver=com.mysql.cj.jdbc.Driver
        
        [logging]
        level=%s
        file=logs/%s.log
        """.formatted(
        appName,
        java.time.LocalDateTime.now(),
        host,
        port,
        debug,
        host,
        port + 1000, // 假设数据库端口
        debug ? "DEBUG" : "INFO",
        appName.toLowerCase()
    );
  }

  /**
   * 异常处理改进演示
   * 展示JDK 14的增强NPE信息和现代异常处理模式
   */
  public void modernExceptionHandling() {
    System.out.println("\n=== 现代异常处理演示 ===");

    // 示例数据
    List<PersonRecord> people = List.of(
        new PersonRecord("张三", 25),
        new PersonRecord("李四", 30),
        new PersonRecord(null, 35) // 故意的问题数据
    );

    // 使用Optional和新特性进行安全处理
    people.stream()
        .map(person -> {
          try {
            // 这里可能抛出NPE，JDK 14+会显示详细信息
            return Optional.of(processPersonSafely(person));
          } catch (Exception e) {
            System.out.println("处理失败: " + e.getMessage());
            return Optional.<String>empty();
          }
        })
        .flatMap(Optional::stream) // JDK 9: Optional转Stream
        .forEach(System.out::println);
  }

  private String processPersonSafely(PersonRecord person) {
    // 使用JDK 11字符串方法进行验证
    if (person.name() == null || person.name().isBlank()) {
      throw new IllegalArgumentException("姓名不能为空");
    }

    return "已处理: " + person.name().strip() + " (年龄: " + person.age() + ")";
  }

  /**
   * 主方法：演示所有新特性
   */
  public static void main(String[] args) {
    System.out.println("=".repeat(60));
    System.out.println("         JDK 9-21 新特性完整演示");
    System.out.println("=".repeat(60));

    JdkNewFeaturesDemo demo = new JdkNewFeaturesDemo();

    // JDK 9 特性
    demo.collectionFactoryMethods();
    demo.streamApiEnhancements();
    demo.optionalEnhancements();

    // JDK 10 特性
    demo.localVariableTypeInference();

    // JDK 11 特性
    demo.stringApiEnhancements();
    demo.httpClientDemo();

    // JDK 12-13 特性
    demo.switchExpressions();

    // JDK 14 特性
    demo.helpfulNullPointerExceptions();
    demo.recordsDemo();

    // JDK 15 特性
    demo.textBlocks();

    // JDK 16 特性
    demo.patternMatchingInstanceof();
    demo.toList();

    // JDK 17 特性
    demo.sealedClassesDemo();

    // JDK 18-19 特性
    demo.jdk18_19Features();

    // JDK 20-21 特性
    demo.jdk20_21Features();
    demo.collectionSequenceEnhancements();

    // 综合演示
    demo.comprehensiveExample();
    demo.performanceComparison();
    demo.practicalUtilities();
    demo.modernExceptionHandling();

    System.out.println("\n" + "=".repeat(60));
    System.out.println("           演示完成");
    System.out.println("=".repeat(60));
  }

  /**
   * 总结：JDK版本特性概览
   *
   * JDK 9 (2017):
   * - 模块系统 (Project Jigsaw)
   * - 集合工厂方法 (List.of, Set.of, Map.of)
   * - Stream API增强 (takeWhile, dropWhile, ofNullable)
   * - Optional API增强 (ifPresentOrElse, stream, or)
   * - 接口私有方法
   *
   * JDK 10 (2018):
   * - var关键字 (局部变量类型推断)
   * - 应用程序类数据共享 (AppCDS)
   * - 垃圾收集器改进
   *
   * JDK 11 (2018) - LTS:
   * - HTTP Client API正式版
   * - String API增强 (isBlank, lines, strip, repeat)
   * - 文件API增强 (Files.readString, writeString)
   * - Optional.isEmpty()
   *
   * JDK 12 (2019):
   * - Switch表达式 (预览)
   * - 微基准测试套件
   * - JVM常量API
   *
   * JDK 13 (2019):
   * - 文本块 (预览)
   * - Switch表达式改进 (yield关键字)
   * - 动态CDS归档
   *
   * JDK 14 (2020):
   * - Switch表达式正式版
   * - Records (预览)
   * - 增强的NullPointerException信息
   * - instanceof模式匹配 (预览)
   *
   * JDK 15 (2020):
   * - 文本块正式版
   * - 密封类 (预览)
   * - 隐藏类
   * - Edwards-Curve数字签名算法
   *
   * JDK 16 (2021):
   * - Records正式版
   * - instanceof模式匹配正式版
   * - 打包工具 (jpackage)
   * - Vector API (孵化)
   *
   * JDK 17 (2021) - LTS:
   * - 密封类正式版
   * - 模式匹配增强
   * - 新的macOS渲染管道
   * - 强封装JDK内部API
   *
   * JDK 18 (2022):
   * - UTF-8默认字符集
   * - 简单Web服务器
   * - 代码片段API
   * - Vector API (第二次孵化)
   *
   * JDK 19 (2022):
   * - Virtual Threads (预览)
   * - 结构化并发 (孵化)
   * - 模式匹配和解构 (预览)
   *
   * JDK 20 (2023):
   * - Scoped Values (孵化)
   * - Record模式匹配 (预览)
   * - Virtual Threads改进
   *
   * JDK 21 (2023) - LTS:
   * - Virtual Threads正式版
   * - 字符串模板 (预览)
   * - 序列化集合
   * - 模式匹配的Switch增强
   *
   * 注意事项：
   * 1. 预览特性需要使用 --enable-preview 编译和运行
   * 2. LTS版本建议生产环境使用 (JDK 8, 11, 17, 21)
   * 3. 某些特性可能需要特定JVM参数才能启用
   * 4. 新特性向下兼容，但某些内部API可能被移除或限制访问
   */
}