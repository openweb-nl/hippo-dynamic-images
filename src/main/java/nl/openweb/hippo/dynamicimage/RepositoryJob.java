/*
 * Copyright 2017 Open Web IT B.V. (https://www.openweb.nl/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.openweb.hippo.dynamicimage;

import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.site.HstServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.concurrent.Callable;


/**
 * Job for a custom action to write to the JCR repository
 * 
 * @author Ivor Boers
 *
 * @param <V>
 */
public abstract class RepositoryJob<V> implements Callable<V> {
    private static final Logger LOG = LoggerFactory.getLogger(RepositoryJob.class);

    public V call() throws JobExecutionException {
        long time0 = System.currentTimeMillis();
        Session session = getSession();
        try {
            long time1 = System.currentTimeMillis();
            long time2;
            V result = doJob(session);
            time2 = System.currentTimeMillis();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Timing of job. getSession=" + (time1 - time0) + "ms. doJob=" + (time2 - time1)
                        + "ms. Total=" + (time2 - time0) + "ms");
            }

            try {
                session.save();
            } catch (RepositoryException e) {
                throw new JobExecutionException("Failed to save the session", e);
            }
            return result;
        } finally {
            session.logout();
        }
    }

    /**
     * Perform the job
     * 
     * @param session:
     *            a valid session. The session is removed after the job has finished.
     * @return the return object of type V
     * @throws JobExecutionException
     */
    public abstract V doJob(Session session) throws JobExecutionException;

    /**
     * Sessions are retrieved from a pooled session. The documentation says "Developers do not need to take care of logging them out."
     * But when you don't close it you'll get "WARN [org.apache.jackrabbit.core.SessionImpl] Unclosed session detected."
     * So please close the session after use
     * @return a Session
     * @throws JobExecutionException when failed to login to repository
     */
    private Session getSession() throws JobExecutionException {
        Credentials writeCred = getCredentials();
        Repository repository = HstServices.getComponentManager().getComponent(Repository.class.getName());
        try {
            return repository.login(writeCred);
        } catch (RepositoryException e) {
            throw new JobExecutionException("Failed to get session", e);
        }
    }

    /**
     * Overwrite if needed
     * @return the credentials
     */
    protected Credentials getCredentials() {
        ComponentManager componentManager = HstServices.getComponentManager();
        return componentManager.getComponent(Credentials.class.getName() + ".writable");
    }


    
    
}
