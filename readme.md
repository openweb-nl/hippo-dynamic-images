#Dynamic Images

The plugin to power up image handling in Hippo CMS or Bloomreach Experience.

##Installation
1. Add dependency to pom.xml

`    <dependency>
      <groupId>nl.openweb.hippo</groupId>
      <artifactId>dynamic-images</artifactId>
      <version>0.01.00-SNAPSHOT</version>
    </dependency>
    <dependency>
      <groupId>org.onehippo.cms7</groupId>
      <artifactId>hippo-cms-gallery-frontend</artifactId>
    </dependency>`
    
2. Add a Spring configuration file in hst-assembly/overrides.

`<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="
       http://www.springframework.org/schema/beans
       http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
  <bean id="nl.openweb.hippo.dynamicimage.strategy.VariantStrategy"
        class="nl.openweb.hippo.dynamicimage.strategy.CenteredVariantStrategy"/>
  <bean id="nl.openweb.hippo.dynamicimage.service.VariantService"
        class="nl.openweb.hippo.dynamicimage.service.DefaultVariantService">
    <property name="timeoutMilliseconds" value="1000"/>
    <property name="variantStrategy" ref="nl.openweb.hippo.dynamicimage.strategy.VariantStrategy"/>
  </bean>
</beans>`

3. Add taglib for JSP.

`<%@ taglib prefix="dynimg" uri="http://www.openweb.nl/hippo/dynamicimages/taglib" %>`

4. Add group sitewriters to domain hippogallery

Add value `sitewriters` to property `hipposys:groups` at path=/hippo:configuration/hippo:domains/hippogallery/hippo:authrole

## Usage

Instead of writing

`<hst:link var="imageUrl" hippobean="${document.image.homepageBanner}"/>`

to generate the URL you can now use

`<dynimg:link var="imageUrl" imagebean="${document.image.original}" width="1312" height="580"/>`

The first time the link is created a new variant will be created for you in the repository.