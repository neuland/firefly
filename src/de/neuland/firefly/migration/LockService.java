package de.neuland.firefly.migration;

import com.google.common.base.Optional;
import de.hybris.platform.servicelayer.cluster.ClusterService;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.neuland.firefly.model.FireflyLockModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Service
@Scope("prototype")
public class LockService {
    private static final Logger LOG = Logger.getLogger(LockService.class);

    @Autowired private LockRepository lockRepository;
    @Autowired private ClusterService clusterService;

    public void lock() throws AlreadyLockedException {
        final Optional<FireflyLockModel> presentLock;
        try {
            presentLock = lockRepository.findLock();
        } catch (FlexibleSearchException e) {
            LOG.warn("Unable to create migration lock since the type is not yet part of the type system.");
            return;
        }

        long clusterIslandId = clusterService.getClusterIslandId();
        if (presentLock.isPresent()) {
            Long lockedByClusterNode = presentLock.get().getClusterNode();
            if (lockedByClusterNode != null && lockedByClusterNode.longValue() != clusterIslandId) {
                throw new AlreadyLockedException(lockedByClusterNode);
            }
        } else {
            FireflyLockModel newLock = lockRepository.create();
            newLock.setClusterNode(clusterIslandId);
            lockRepository.save(newLock);

            LOG.info("Created migration lock for cluster node " + clusterIslandId);
        }
    }

    public void unlock() {
        try {
            Optional<FireflyLockModel> lock = lockRepository.findLock();
            long clusterIslandId = clusterService.getClusterIslandId();
            if (lock.isPresent() && lock.get().getClusterNode() == clusterIslandId) {
                final Long clusterNode = lock.get().getClusterNode();

                lockRepository.remove(lock.get());
                LOG.info("Removed migration lock for cluster node " + clusterNode);
            }
        } catch (FlexibleSearchException e) {
            LOG.warn("Unable to create migration lock since the type is not yet part of the type system.");
            return;
        }
    }

    public static class AlreadyLockedException extends Exception {
        private AlreadyLockedException(long clusterNode) {
            super("Lock already set by cluster node " + clusterNode);
        }
    }
}
