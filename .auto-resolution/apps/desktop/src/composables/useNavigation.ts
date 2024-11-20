import { useUserStore } from '~/stores'
import type {NavigationItem} from "~/types";
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import SupportIcon from "~/components/icons/SupportIcon.vue";
import DashboardIcon from "~/components/icons/DashboardIcon.vue";
import { ShieldPlus, Telescope } from 'lucide-vue-next';

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
        name: 'Roadmaps',
        icon: RoadmapIcon,
        route: '/my-roadmaps',
        roles: ['USER', 'ADMIN', 'MODERATOR']
      },
      {
        name: 'Explore',
        icon: Telescope,
        route: '/explore',
        roles: ['USER', 'ADMIN', 'MODERATOR']
      },
      {
        name: 'Admin',
        icon: ShieldPlus,
        route: '/admin',
        roles: ['ADMIN']
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