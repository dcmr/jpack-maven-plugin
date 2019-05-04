package com.blinkfox.jpack;

import static com.blinkfox.jpack.PackBuildMojo.HOME_DIR_NAME;

import com.blinkfox.jpack.utils.Logger;

import java.io.File;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugin.testing.MojoRule;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * 按顺序执行各个单元测试方法.
 *
 * @author blinkfox on 2019-05-03.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PackBuildMojoTest {

    /**
     * MojoRule.
     */
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() {
            Logger.initSetLog(new SystemStreamLog());
        }

        @Override
        protected void after() {
            // no code.
        }
    };

    /**
     * 测试 jpack 插件.
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecute1() throws Exception {
        // 获取测试的 pom.xml
        String baseDir = PlexusTestCase.getBasedir();
        File packPom = new File(baseDir, "src/test/resources/jpack-test-pom.xml");
        Assert.assertTrue(packPom.exists());

        // 获取 mojo 并执行.
        PackBuildMojo packMojo = (PackBuildMojo) rule.lookupMojo("build", packPom);
        Assert.assertNotNull(packMojo);
        this.setPackMojoProperties(baseDir, packMojo);
        packMojo.execute();
    }

    /**
     * 测试 jpack 插件.
     *
     * @throws Exception 异常
     */
    @Test
    public void testExecute2WithoutPlatforms() throws Exception {
        // 获取测试的 pom.xml
        String baseDir = PlexusTestCase.getBasedir();
        PackBuildMojo packMojo = (PackBuildMojo) rule.lookupMojo("build",
                new File(baseDir, "src/test/resources/jpack-test-pom-simple.xml"));

        // 预先生成一些目录.
        File targetDir = new File(baseDir + File.separator + "target");
        String homeDir = targetDir.getAbsolutePath() + File.separator + HOME_DIR_NAME;
        FileUtils.mkdir(homeDir);
        String platformPath = homeDir + File.separator + "windows";
        FileUtils.mkdir(platformPath);

        // 设置一些属性.
        packMojo.setTargetDir(targetDir);
        packMojo.setArtifactId("jpack-test-simple");
        packMojo.setFinalName("jpack-test-simple-1.3.5");
        packMojo.setDescription("这是 jpack-test-simple 项目的测试 description.");

        // 执行该 mojo.
        packMojo.execute();
    }

    /**
     * 设置 PackBuildMojo 对象的一些用于测试的属性值.
     *
     * @param baseDir 项目基础目录
     * @param packMojo PackBuildMojo 对象
     */
    private void setPackMojoProperties(String baseDir, PackBuildMojo packMojo) {
        packMojo.setTargetDir(new File(baseDir + File.separator + "target"));
        packMojo.setArtifactId("jpack-test");
        packMojo.setFinalName("jpack-test-1.2.3-SNAPSHOT");
        packMojo.setDescription("这是 jpack-test 项目的测试 description.");
    }

}
