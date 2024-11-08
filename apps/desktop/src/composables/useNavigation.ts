import { useUserStore } from '~/stores'
import type {NavigationItem} from "~/types";
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import SupportIcon from "~/components/icons/SupportIcon.vue";
import DashboardIcon from "~/components/icons/DashboardIcon.vue";
import CubeIcon from "~/components/icons/CubeIcon.vue";

export function useNavigation() {
  const userStore = useUserStore()

  const hasRole = (requiredRoles?: string[]) => {
    if (!requiredRoles || requiredRoles.length === 0) return true
    return userStore.user?.roles.some(role => requiredRoles.includes(role))
  }

  const navigationItems = computed(() => {
    const items: NavigationItem[] = [
      {
        name: 'Dashboard',
        icon: DashboardIcon,
        route: '/',
        roles: ['USER', 'ADMIN', 'MODERATOR']
      },
      {
        name: 'Analytics',
        icon: CubeIcon,
        route: '/analytics',
        roles: ['ADMIN']
      },
      {
        name: 'Roadmaps',
        icon: RoadmapIcon,
        route: '/roadmaps',
        roles: ['USER', 'ADMIN', 'MODERATOR']
      },
      {
        name: 'Support',
        icon: SupportIcon,
        route: '/settings'
      }
    ]

    return items.filter(item => hasRole(item.roles))
  })

  return {
    navigationItems,
    hasRole
  }
}