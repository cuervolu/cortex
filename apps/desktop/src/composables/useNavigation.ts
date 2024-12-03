import { useUserStore } from '~/stores'
import type {NavigationItem} from "~/types";
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import SupportIcon from "~/components/icons/SupportIcon.vue";
import DashboardIcon from "~/components/icons/DashboardIcon.vue";
import { ShieldPlus, Telescope } from 'lucide-vue-next';
import MentorshipIcon from '~/components/icons/MentorshipIcon.vue';

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
        roles: ['USER', 'ADMIN', 'MODERATOR'],
        isCustomIcon: true
      },
      {
        name: 'Roadmaps',
        icon: RoadmapIcon,
        route: '/my-roadmaps',
        roles: ['USER', 'ADMIN', 'MODERATOR'],
        isCustomIcon: true
      },
      {
        name: 'Explorar',
        icon: Telescope,
        route: '/explore',
        roles: ['USER', 'ADMIN', 'MODERATOR'],
        isCustomIcon: false
      },
      {
        name: 'Mentorias',
        icon: MentorshipIcon,
        route: '/mentorship',
        roles: ['MENTOR','ADMIN', 'MODERATOR'],
        isCustomIcon: true
      },
      {
        name: 'Admin',
        icon: ShieldPlus,
        route: '/admin',
        roles: ['ADMIN'],
        isCustomIcon: false
      },
      {
        name: 'Soporte',
        icon: SupportIcon,
        route: '/settings',
        isCustomIcon: true
      }
    ]

    return items.filter(item => hasRole(item.roles))
  })

  return {
    navigationItems,
    hasRole
  }
}